let userAccounts = [];
let activeAccount = null;

const token = localStorage.getItem('token');
const currentUserId = localStorage.getItem('userId');

// 📦 LOAD ACCOUNTS
async function loadAccounts() {
    try {
        const res = await fetch(`/api/accounts/user/${currentUserId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        const data = await res.json();
        userAccounts = data;

        let html = '';
        let totalBalance = 0;

        data.forEach(acc => {
            totalBalance += Number(acc.balance);

            html += `
            <div class="account-item" onclick="selectAccount('${acc.accountNumber}')">
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

        document.getElementById('accounts').innerHTML = html;
        document.getElementById('totalBalance').textContent =
            totalBalance.toLocaleString('en-IN');

        if (data.length > 0) {
            selectAccount(data[0].accountNumber);
        }

    } catch (err) {
        console.error(err);
        alert("Error loading accounts");
    }
}

// 🎯 SELECT ACCOUNT
function selectAccount(accountNumber) {
    activeAccount = accountNumber;
    document.getElementById("from").value = accountNumber;

    loadTransactions("all");
}

// 💸 TRANSFER
async function transfer() {
    const from = document.getElementById("from").value;
    const to = document.getElementById("to").value;
    const amount = document.getElementById("amount").value;

    if (!from || !to || !amount) {
        alert("Fill all fields");
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

            await loadAccounts();
            await loadTransactions("all");
        } else {
            alert(msg);
        }

    } catch (err) {
        console.error(err);
        alert("Server error");
    }
}

// 📜 TRANSACTION HISTORY + GLOBAL TOTALS
async function loadTransactions(filter = "all") {
    try {
        const res = await fetch(`/api/transactions/all/${currentUserId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        const data = await res.json();

        let filtered = data;

        if (filter === "sent") {
            filtered = data.filter(txn =>
                userAccounts.some(acc => acc.accountNumber === txn.senderAccount)
            );
        }

        if (filter === "received") {
            filtered = data.filter(txn =>
                userAccounts.some(acc => acc.accountNumber === txn.receiverAccount)
            );
        }

        const container = document.getElementById("txnList");
        container.innerHTML = "";

        if (!filtered.length) {
            container.innerHTML = "<p>No transactions found</p>";
        } else {
            filtered.forEach(txn => {
                const isCredit = userAccounts.some(acc => acc.accountNumber === txn.receiverAccount);
                const sign = isCredit ? "+" : "-";
                const color = isCredit ? "lime" : "red";

                const div = document.createElement("div");

                div.innerHTML = `
                    <div style="padding:10px;border-bottom:1px solid #333;">
                        <b>${txn.senderAccount} → ${txn.receiverAccount}</b><br/>
                        <span style="color:${color}">
                            ${sign} ₹${Number(txn.amount).toLocaleString('en-IN')}
                        </span>
                    </div>
                `;

                container.appendChild(div);
            });
        }

        // 🔥 GLOBAL TOTALS (ALL ACCOUNTS, ALWAYS FROM UNFILTERED DATA)
// 🔥 DASHBOARD SUMMARY
let totalSent = 0;
let totalReceived = 0;

let sentCount = 0;
let receivedCount = 0;

data.forEach(txn => {

    const isSent = userAccounts.some(acc =>
        acc.accountNumber === txn.senderAccount
    );

    const isReceived = userAccounts.some(acc =>
        acc.accountNumber === txn.receiverAccount
    );

    if (isSent) {
        totalSent += Number(txn.amount);
        sentCount++;
    }

    if (isReceived) {
        totalReceived += Number(txn.amount);
        receivedCount++;
    }

});

document.getElementById("totalSent").textContent =
    "₹" + totalSent.toLocaleString("en-IN");

document.getElementById("totalReceived").textContent =
    "₹" + totalReceived.toLocaleString("en-IN");

document.getElementById("totalTransactions").textContent =
    data.length;

// Update counts if these elements exist
const sentCountEl = document.getElementById("sentCount");
if (sentCountEl) {
    sentCountEl.textContent = sentCount;
}

const receivedCountEl = document.getElementById("receivedCount");
if (receivedCountEl) {
    receivedCountEl.textContent = receivedCount;
}

function showAll() {
    loadTransactions("all");
}

function showSent() {
    loadTransactions("sent");
}

function showReceived() {
    loadTransactions("received");
}

// 🚀 INIT
window.onload = () => {
    loadAccounts();
};