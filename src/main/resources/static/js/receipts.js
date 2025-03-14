
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
                <button class="btn btn-sm btn-secondary view-btn" data-id="${receipt.id}">View</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${receipt.id}">Delete</button>
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

        const receiptDate = new Date(receipt.dateTime);
        const formattedDate = receiptDate.toLocaleDateString() + ' ' + receiptDate.toLocaleTimeString();

        let itemsHtml = '';
        receipt.items.forEach(item => {
            itemsHtml += `
                <tr>
                    <td>${item.productName}</td>
                    <td>${item.quantity}</td>
                    <td>$${parseFloat(item.price).toFixed(2)}</td>
                    <td>$${parseFloat(item.total).toFixed(2)}</td>
                </tr>
            `;
        });

        receiptModalContent.innerHTML = `
            <div class="receipt-details">
                <h2>Receipt #${receipt.id}</h2>
                
                <div class="receipt-info">
                    <div><strong>Date:</strong> ${formattedDate}</div>
                    <div><strong>Cashier:</strong> ${receipt.cashierName || '-'}</div>
                    <div><strong>Payment Method:</strong> ${receipt.paymentMethod || '-'}</div>
                </div>
                
                <h3>Items</h3>
                <table class="table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Qty</th>
                            <th>Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${itemsHtml}
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="3" class="text-right"><strong>Subtotal:</strong></td>
                            <td>$${(parseFloat(receipt.total) - parseFloat(receipt.taxAmount)).toFixed(2)}</td>
                        </tr>
                        <tr>
                            <td colspan="3" class="text-right"><strong>Tax (10%):</strong></td>
                            <td>$${parseFloat(receipt.taxAmount).toFixed(2)}</td>
                        </tr>
                        <tr>
                            <td colspan="3" class="text-right"><strong>Total:</strong></td>
                            <td><strong>$${parseFloat(receipt.total).toFixed(2)}</strong></td>
                        </tr>
                    </tfoot>
                </table>
                
                <div class="modal-actions">
                    <button class="btn btn-primary" onclick="printReceiptDetails()">Print</button>
                </div>
            </div>
        `;

        receiptModal.style.display = 'block';
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
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
