let userAccounts = [];
let activeAccount = null;

const token = localStorage.getItem('token');
const currentUserId = localStorage.getItem('userId');

// Redirect to login if not authenticated
if (!token || !currentUserId) {
    window.location.href = '/';
}

// LOAD ACCOUNTS
async function loadAccounts() {
    try {
        const res = await fetch(`/api/accounts/user/${currentUserId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) throw new Error('Failed to load accounts');

        const data = await res.json();
        userAccounts = data;

        let html = '';
        let totalBalance = 0;

        data.forEach((acc, idx) => {
            totalBalance += Number(acc.balance);
            const isActive = idx === 0 ? 'active' : '';

            html += `
            <div class="account-item ${isActive}" onclick="selectAccount('${acc.accountNumber}')" data-acc="${acc.accountNumber}">
                <div class="acc-left">
                    <div class="acc-icon">💳</div>
                    <div>
                        <div class="acc-number">${acc.accountNumber}</div>
                        <div class="acc-type">${acc.accountType}</div>
                    </div>
                </div>
                <div class="acc-bal">
                    ₹${Number(acc.balance).toLocaleString('en-IN')}
                </div>
            </div>`;
        });

        document.getElementById('accounts').innerHTML = html || '<div class="empty-state">No accounts found</div>';
        document.getElementById('totalBalance').textContent = '₹' + totalBalance.toLocaleString('en-IN');

        if (data.length > 0) {
            selectAccount(data[0].accountNumber);
        }

    } catch (err) {
        console.error(err);
        document.getElementById('accounts').innerHTML = '<div class="empty-state">Error loading accounts</div>';
    }
}

// SELECT ACCOUNT
function selectAccount(accountNumber) {
    activeAccount = accountNumber;
    document.getElementById('from').value = accountNumber;

    // Highlight active account
    document.querySelectorAll('.account-item').forEach(el => {
        el.classList.toggle('active', el.dataset.acc === accountNumber);
    });

    loadTransactions('all');
}

// TRANSFER
async function transfer() {
    const from = document.getElementById('from').value;
    const to = document.getElementById('to').value;
    const amount = document.getElementById('amount').value;

    if (!from || !to || !amount) {
        alert('Fill all fields');
        return;
    }

    if (from === to) {
        alert('Cannot transfer to the same account');
        return;
    }

    try {
        const res = await fetch('/api/transactions/transfer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                fromAccount: from,
                toAccount: to,
                amount: parseFloat(amount),
                userId: Number(currentUserId)
            })
        });

        const msg = await res.text();

        if (res.ok) {
            alert(`💸 ₹${amount} sent from ${from}`);
            alert(`✅ ₹${amount} received in ${to}`);

            document.getElementById('to').value = '';
            document.getElementById('amount').value = '';

            await loadAccounts();
            await loadTransactions('all');
        } else {
            alert(msg || 'Transfer failed');
        }

    } catch (err) {
        console.error(err);
        alert('Server error during transfer');
    }
}

// TRANSACTION HISTORY + GLOBAL TOTALS
async function loadTransactions(filter = 'all') {
    try {
        const res = await fetch(`/api/transactions/all/${currentUserId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) throw new Error('Failed to load transactions');

        const data = await res.json();

        let filtered = data;

        if (filter === 'sent') {
            filtered = data.filter(txn =>
                userAccounts.some(acc => acc.accountNumber === txn.senderAccount)
            );
        }

        if (filter === 'received') {
            filtered = data.filter(txn =>
                userAccounts.some(acc => acc.accountNumber === txn.receiverAccount)
            );
        }

        // Update filter buttons
        document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
        document.getElementById('btn' + filter.charAt(0).toUpperCase() + filter.slice(1)).classList.add('active');

        const container = document.getElementById('txnList');
        container.innerHTML = '';

        if (!filtered.length) {
            container.innerHTML = '<div class="empty-state">No transactions found</div>';
        } else {
            filtered.forEach(txn => {
                const isCredit = userAccounts.some(acc => acc.accountNumber === txn.receiverAccount);
                const sign = isCredit ? '+' : '-';
                const amtClass = isCredit ? 'credit' : 'debit';
                const icon = isCredit ? '⬇️' : '⬆️';
                const label = isCredit ? 'Received' : 'Sent';
                const iconClass = isCredit ? 'credit' : 'debit';

                const dateStr = txn.timestamp
                    ? new Date(txn.timestamp).toLocaleString('en-IN', {
                        day: '2-digit', month: 'short', year: 'numeric',
                        hour: '2-digit', minute: '2-digit'
                    })
                    : '';

                const div = document.createElement('div');
                div.className = 'txn-row';

                div.innerHTML = `
                    <div class="txn-left">
                        <div class="txn-icon ${iconClass}">${icon}</div>
                        <div class="txn-info">
                            <div class="txn-accounts">${label} · ${txn.senderAccount} → ${txn.receiverAccount}</div>
                            <div class="txn-date">${dateStr || 'Just now'}</div>
                        </div>
                    </div>
                    <div class="txn-right">
                        <div class="txn-amt ${amtClass}">${sign} ₹${Number(txn.amount).toLocaleString('en-IN')}</div>
                        <div class="txn-status">Completed</div>
                    </div>
                `;

                container.appendChild(div);
            });
        }

        // DASHBOARD SUMMARY (computed from full unfiltered data)
        let totalSent = 0;
        let totalReceived = 0;
        let sentCount = 0;
        let receivedCount = 0;

        data.forEach(txn => {
            const isSent = userAccounts.some(acc => acc.accountNumber === txn.senderAccount);
            const isReceived = userAccounts.some(acc => acc.accountNumber === txn.receiverAccount);

            if (isSent) {
                totalSent += Number(txn.amount);
                sentCount++;
            }
            if (isReceived) {
                totalReceived += Number(txn.amount);
                receivedCount++;
            }
        });

        const totalSentEl = document.getElementById('totalSent');
        if (totalSentEl) totalSentEl.textContent = '₹' + totalSent.toLocaleString('en-IN');

        const totalReceivedEl = document.getElementById('totalReceived');
        if (totalReceivedEl) totalReceivedEl.textContent = '₹' + totalReceived.toLocaleString('en-IN');

        const totalTransactionsEl = document.getElementById('totalTransactions');
        if (totalTransactionsEl) totalTransactionsEl.textContent = data.length;

    } catch (err) {
        console.error(err);
        document.getElementById('txnList').innerHTML = '<div class="empty-state">Error loading transactions</div>';
    }
}

function showAll() { loadTransactions('all'); }
function showSent() { loadTransactions('sent'); }
function showReceived() { loadTransactions('received'); }

// INIT
window.onload = () => {
    loadAccounts();
};