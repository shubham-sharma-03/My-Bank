let userAccounts = [];
let activeAccount = null;
let accountsLoaded = false;

const token = localStorage.getItem('token');
const currentUserId = localStorage.getItem('userId');

// 📦 LOAD ACCOUNTS (✅ FIXED)
async function loadAccounts() {

```
if (!currentUserId) {
    document.getElementById('accounts').innerHTML =
        `<div class="no-accounts">
            <div style="font-size:32px">⚠️</div>
            <p>Session expired — please log in again</p>
        </div>`;
    return;
}

try {
    const res = await fetch(`/api/accounts/user/${currentUserId}`, {
        headers: { 'Authorization': 'Bearer ' + token }
    });

    const data = await res.json();

    userAccounts = data;
    accountsLoaded = true;

    let total = 0;
    let html = '';

    data.forEach(acc => {
        total += Number(acc.balance);

        html += `
        <div class="account-item">

            <div class="acc-left">
                <div class="acc-icon">💳</div>
                <div>
                    <div class="acc-number">${acc.accountNumber}</div>
                    <div class="acc-type">${acc.accountType || 'Savings'}</div>
                </div>
            </div>

            <div class="account-actions">
                <div class="acc-bal">
                    ₹${Number(acc.balance).toLocaleString('en-IN')}
                </div>

                <!-- ✅ DELETE BUTTON -->
                <button class="delete-btn"
                    onclick="deleteAccount('${acc.accountNumber}', event)">
                    ❌
                </button>
            </div>

        </div>`;
    });

    document.getElementById('accounts').innerHTML = html;
    document.getElementById('totalBalance').textContent = total.toLocaleString('en-IN');

} catch (err) {
    console.error(err);
    document.getElementById('accounts').innerHTML = "Error loading accounts";
}
```

}

// ❌ DELETE ACCOUNT (✅ FIXED)
async function deleteAccount(accountNumber, e) {

```
if (e) e.stopPropagation();

if (!confirm("Are you sure you want to delete this account?")) return;

try {
    const res = await fetch(`/api/accounts/delete/${accountNumber}`, {
        method: "DELETE",
        headers: { 'Authorization': 'Bearer ' + token }
    });

    const msg = await res.text();
    alert(msg);

    loadAccounts();

} catch (err) {
    console.error(err);
    alert("Error deleting account");
}
```

}

// 💸 TRANSFER
async function transfer() {
const from = document.getElementById("from").value;
const to = document.getElementById("to").value;
const amount = document.getElementById("amount").value;

```
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
            amount: parseFloat(amount)
        })
    });

    if (res.ok) {
        alert("✅ Transfer Successful");
        loadAccounts();
    } else {
        const msg = await res.text();
        alert(msg);
    }

} catch (err) {
    console.error(err);
    alert("Server error");
}
```

}

// 📜 LOAD TRANSACTIONS
async function loadTransactions(accountNumber) {
try {
const res = await fetch(`/api/transactions/history/${accountNumber}`, {
headers: { 'Authorization': 'Bearer ' + token }
});

```
    const data = await res.json();

    const container = document.getElementById("txnList");
    if (!container) return;

    container.innerHTML = "";

    data.forEach(txn => {
        const div = document.createElement("div");

        const isCredit = txn.receiverAccount === accountNumber;
        const sign = isCredit ? "+" : "-";

        div.innerHTML = `
            <div>
                ${txn.senderAccount} → ${txn.receiverAccount}<br>
                ${sign} ₹${txn.amount}
            </div>
        `;

        container.appendChild(div);
    });

} catch (err) {
    console.error(err);
}
```

}

// 🚀 INIT
window.onload = () => {
loadAccounts();
};

<div style="display:flex; align-items:center; gap:10px;">
    <div class="acc-bal">
        ₹${Number(acc.balance).toLocaleString('en-IN')}
    </div>

    <button onclick="deleteAccount('${acc.accountNumber}', event)"
        style="background:red; color:white; border:none; padding:5px 8px; border-radius:6px;">
        ❌
    </button>
</div>