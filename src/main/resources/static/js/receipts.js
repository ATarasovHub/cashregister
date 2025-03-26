
const receiptsTable = document.getElementById('receipts-table');
const receiptModal = document.getElementById('receipt-modal');
const receiptModalContent = document.getElementById('receipt-modal-content');
const receiptModalClose = document.getElementById('receipt-modal-close');

async function fetchReceipts() {
    try {
        const response = await fetch('/receipts/api/all');
        if (!response.ok) {
            throw new Error('Failed to fetch receipts');
        }
        const receipts = await response.json();
        renderReceiptsTable(receipts);
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

function renderReceiptsTable(receipts) {
    const tableBody = receiptsTable.querySelector('tbody');
    tableBody.innerHTML = '';

    if (receipts.length === 0) {
        const emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="6" class="text-center">No receipts found.</td>';
        tableBody.appendChild(emptyRow);
        return;
    }

    receipts.forEach(receipt => {
        const date = new Date(receipt.dateTime);
        const formattedDate = date.toLocaleDateString() + ' ' + date.toLocaleTimeString();

        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${receipt.id}</td>
            <td>${formattedDate}</td>
            <td>${receipt.cashierName || '-'}</td>
            <td>${receipt.items.length} items</td>
            <td>$${parseFloat(receipt.total).toFixed(2)}</td>
            <td>
              <div class="action-buttons">
                <button class="btn btn-sm btn-secondary view-btn" data-id="${receipt.id}">View</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${receipt.id}">Delete</button>
              </div>
            </td>

        `;

        tableBody.appendChild(row);
    });

    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.addEventListener('click', () => viewReceipt(btn.dataset.id));
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => deleteReceipt(btn.dataset.id));
    });
}

async function viewReceipt(receiptId) {
    try {
        const response = await fetch(`/receipts/api/${receiptId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch receipt details');
        }
        const receipt = await response.json();
        showReceiptModal(receipt);
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

function showReceiptModal(receipt) {
    const receiptDate = new Date(receipt.dateTime);
    const formattedDate = receiptDate.toLocaleDateString() + ' ' + receiptDate.toLocaleTimeString();

    const paymentReceived = parseFloat(receipt.paymentReceived ?? 0);
    const change = parseFloat(receipt.changeAmount ?? 0);
    const taxAmount = parseFloat(receipt.taxAmount ?? 0);
    const total = parseFloat(receipt.total ?? 0);

    let itemsHtml = '';
    receipt.items.forEach(item => {
        const price = parseFloat(item.price ?? 0);
        const itemTotal = parseFloat(item.total ?? (price * item.quantity));
        itemsHtml += `
            <div class="receipt-item">
                <div>${item.productName}</div>
                <div>${item.quantity} x $${price.toFixed(2)}</div>
                <div>$${itemTotal.toFixed(2)}</div>
            </div>
        `;
    });

    receiptModalContent.innerHTML = `
        <div class="receipt-preview">
            <div class="receipt-header">
                <h2>Store Receipt</h2>
                <div class="receipt-id">Receipt #${receipt.id}</div>
                <div class="receipt-date">${formattedDate}</div>
                <div class="receipt-cashier">Cashier: ${receipt.cashierName ?? '-'}</div>
                <div class="receipt-payment">Payment: ${receipt.paymentMethod ?? '-'}</div>
                <div class="receipt-received">Received: $${paymentReceived.toFixed(2)}</div>
                <div class="receipt-change">Change: $${change.toFixed(2)}</div>
            </div>
            <div class="receipt-items">
                ${itemsHtml}
            </div>
            <div class="receipt-total">
                Subtotal: $${(total - taxAmount).toFixed(2)}<br>
                Tax (10%): $${taxAmount.toFixed(2)}<br>
                Total: $${total.toFixed(2)}
            </div>
            <div class="receipt-footer">
                Thank you for shopping with us!
            </div>
        </div>
    `;

    receiptModal.style.display = 'block';
    receiptModal.classList.add('show');
}

function printReceipt() {
    const printWindow = window.open('', '_blank');
    printWindow.document.write('<html><head><title>Receipt</title>');
    printWindow.document.write('<style>');
    printWindow.document.write(`
        body { font-family: 'Courier New', monospace; margin: 20px; }
        .receipt-preview { max-width: 300px; margin: 0 auto; }
        .receipt-header { text-align: center; margin-bottom: 20px; }
        .receipt-items { margin-bottom: 20px; }
        .receipt-item { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .receipt-total { border-top: 1px dashed #000; padding-top: 10px; }
        .receipt-footer { margin-top: 20px; text-align: center; }
    `);
    printWindow.document.write('</style></head><body>');
    printWindow.document.write(receiptModalContent.innerHTML);
    printWindow.document.write('</body></html>');
    printWindow.document.close();

    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
}

function closeReceiptModal() {
    receiptModal.style.display = 'none';
    receiptModal.classList.remove('show');
}




function printReceiptDetails() {
    const printWindow = window.open('', '_blank');
    printWindow.document.write('<html><head><title>Receipt</title>');
    printWindow.document.write('<style>');
    printWindow.document.write(`
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2, h3 { margin-top: 0; }
        .receipt-info { margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
        tfoot td { border-top: 2px solid #000; }
        .text-right { text-align: right; }
    `);
    printWindow.document.write('</style></head><body>');
    printWindow.document.write(receiptModalContent.innerHTML);

    printWindow.document.write('<script>document.querySelector(".modal-actions").remove();</script>');

    printWindow.document.write('</body></html>');
    printWindow.document.close();

    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
}

async function deleteReceipt(receiptId) {
    if (!confirm('Are you sure you want to delete this receipt?')) {
        return;
    }

    try {
        const response = await fetch(`/receipts/api/${receiptId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete receipt');
        }

        showToast('Receipt deleted successfully');
        fetchReceipts();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

function closeReceiptModal() {
    receiptModal.style.display = 'none';
}

function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;

    document.body.appendChild(toast);

    toast.offsetHeight;

    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    fetchReceipts();

    receiptModalClose.addEventListener('click', closeReceiptModal);

    window.addEventListener('click', (event) => {
        if (event.target === receiptModal) {
            closeReceiptModal();
        }
    });
});
