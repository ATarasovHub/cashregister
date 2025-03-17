const productsGrid = document.getElementById('products-grid');
const cartItemsContainer = document.getElementById('cart-items');
const subtotalElement = document.getElementById('subtotal');
const taxElement = document.getElementById('tax');
const totalElement = document.getElementById('total');
const checkoutBtn = document.getElementById('checkout-btn');
const clearCartBtn = document.getElementById('clear-cart-btn');
const cashierNameInput = document.getElementById('cashier-name');
const paymentMethodSelect = document.getElementById('payment-method');
const receiptModal = document.getElementById('receipt-modal');
const receiptModalCloseBtn = document.querySelector('#receipt-modal .close-btn');
const printReceiptBtn = document.getElementById('print-receipt-btn');
const receiptContent = document.getElementById('receipt-content');

let products = [];
let cartItems = [];
const TAX_RATE = 0.10; // 10% tax rate

async function fetchProducts() {
    try {
        const response = await fetch('/products/api/all');
        if (!response.ok) {
            throw new Error('Failed to fetch products');
        }
        products = await response.json();
        renderProducts();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

function renderProducts() {
    productsGrid.innerHTML = '';

    products.forEach(product => {
        const productCard = document.createElement('div');
        productCard.className = 'product-card';
        productCard.dataset.id = product.id;

        productCard.innerHTML = `
            <h3>${product.name}</h3>
            <div class="price">$${parseFloat(product.price).toFixed(2)}</div>
            <div class="stock">${product.stockQuantity} in stock</div>
        `;

        productCard.addEventListener('click', () => addToCart(product));

        productsGrid.appendChild(productCard);
    });
}

function addToCart(product) {
    if (product.stockQuantity <= 0) {
        showToast('Product is out of stock', 'error');
        return;
    }

    const existingItem = cartItems.find(item => item.productId === product.id);

    if (existingItem) {
        if (existingItem.quantity < product.stockQuantity) {
            existingItem.quantity += 1;
            renderCart();
            showToast(`Added ${product.name} to cart`);
        } else {
            showToast('Cannot add more of this product - stock limit reached', 'warning');
        }
    } else {
        cartItems.push({
            productId: product.id,
            productName: product.name,
            price: product.price,
            quantity: 1,
            maxQuantity: product.stockQuantity
        });
        renderCart();
        showToast(`Added ${product.name} to cart`);
    }
}

function updateCartItemQuantity(productId, change) {
    productId = Number(productId);

    const itemIndex = cartItems.findIndex(item => item.productId === productId);
    console.log('Updating quantity for product:', productId, 'change:', change, 'found at index:', itemIndex);

    if (itemIndex !== -1) {
        const item = cartItems[itemIndex];
        const newQuantity = item.quantity + change;
        console.log('Current quantity:', item.quantity, 'New quantity:', newQuantity, 'Max:', item.maxQuantity);

        if (newQuantity > 0 && newQuantity <= item.maxQuantity) {
            item.quantity = newQuantity;
            renderCart();
            showToast(`Updated ${item.productName} quantity to ${newQuantity}`);
        } else if (newQuantity <= 0) {
            if (confirm('Remove item from cart?')) {
                cartItems.splice(itemIndex, 1);
                renderCart();
                showToast(`Removed ${item.productName} from cart`);
            }
        } else {
            showToast('Cannot add more of this product - stock limit reached', 'warning');
        }
    }
}

function removeCartItem(productId) {
    const itemIndex = cartItems.findIndex(item => item.productId === productId);
    if (itemIndex !== -1) {
        const item = cartItems[itemIndex];
        cartItems.splice(itemIndex, 1);
        renderCart();
        showToast(`Removed ${item.productName} from cart`);
    }
}

function renderCart() {
    cartItemsContainer.innerHTML = '';

    if (cartItems.length === 0) {
        cartItemsContainer.innerHTML = '<div class="empty-cart">Cart is empty</div>';
        subtotalElement.textContent = '$0.00';
        taxElement.textContent = '$0.00';
        totalElement.textContent = '$0.00';
        disableCheckout();
        return;
    }

    let subtotal = 0;

    cartItems.forEach(item => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;

        const cartItemElement = document.createElement('div');
        cartItemElement.className = 'cart-item';

        cartItemElement.innerHTML = `
            <div class="cart-item-info">
                <div class="item-name">${item.productName}</div>
                <div class="item-price">$${parseFloat(item.price).toFixed(2)} × ${item.quantity}</div>
            </div>
            <div class="quantity-controls">
                <button class="quantity-btn decrease" data-id="${item.productId}">-</button>
                <span>${item.quantity}</span>
                <button class="quantity-btn increase" data-id="${item.productId}">+</button>
            </div>
            <div class="item-total">$${itemTotal.toFixed(2)}</div>
            <button class="remove-btn" data-id="${item.productId}">×</button>
        `;

        cartItemsContainer.appendChild(cartItemElement);

        // Event listeners for quantity controls and removal
        const decreaseBtn = cartItemElement.querySelector('.quantity-btn.decrease');
        const increaseBtn = cartItemElement.querySelector('.quantity-btn.increase');
        const removeBtn = cartItemElement.querySelector('.remove-btn');

        decreaseBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            updateCartItemQuantity(e.target.dataset.id, -1);
        });

        increaseBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            updateCartItemQuantity(e.target.dataset.id, 1);
        });

        removeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (confirm('Remove item from cart?')) {
                removeCartItem(Number(e.target.dataset.id));
            }
        });
    });

    const tax = subtotal * TAX_RATE;
    const total = subtotal + tax;

    subtotalElement.textContent = `$${subtotal.toFixed(2)}`;
    taxElement.textContent = `$${tax.toFixed(2)}`;
    totalElement.textContent = `$${total.toFixed(2)}`;

    enableCheckout();
}


function enableCheckout() {
    checkoutBtn.disabled = false;
}

function disableCheckout() {
    checkoutBtn.disabled = true;
}

function clearCart() {
    cartItems = [];
    renderCart();
}

async function processCheckout() {
    if (cartItems.length === 0) {
        showToast('Cart is empty', 'warning');
        return;
    }

    const cashierName = document.getElementById('cashier-name').value.trim();
    if (!cashierName) {
        showToast('Please enter cashier name', 'warning');
        document.getElementById('cashier-name').focus();
        return;
    }

    const paymentMethod = document.getElementById('payment-method').value;
    if (!paymentMethod) {
        showToast('Please select payment method', 'warning');
        document.getElementById('payment-method').focus();
        return;
    }

    const paymentReceivedInput = document.getElementById('payment-received');
    const paymentReceivedValue = paymentReceivedInput.value.trim();
    if (!paymentReceivedValue) {
        showToast('Please enter payment amount', 'warning');
        paymentReceivedInput.focus();
        return;
    }
    const paymentReceived = parseFloat(paymentReceivedValue);
    if (isNaN(paymentReceived)) {
        showToast('Invalid payment amount', 'error');
        paymentReceivedInput.focus();
        return;
    }

    const total = parseFloat(totalElement.textContent.replace('$', ''));
    if (paymentReceived < total) {
        showToast(`Insufficient payment. Total is $${total.toFixed(2)}`, 'warning');
        paymentReceivedInput.focus();
        return;
    }
    const change = paymentReceived - total;

    const receiptData = {
        cashierName: cashierName,
        paymentMethod: paymentMethod,
        paymentReceived: paymentReceived,
        change: change,
        items: cartItems.map(item => ({
            productId: item.productId,
            quantity: item.quantity
        }))
    };

    try {
        const response = await fetch('/receipts/api', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(receiptData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to process checkout');
        }

        const receipt = await response.json();

        receipt.paymentReceived = paymentReceived;
        receipt.change = change;

        showReceipt(receipt);
        clearCart();
        fetchProducts();
        showToast('Checkout successful!', 'success');

        document.getElementById('cashier-name').value = '';
        document.getElementById('payment-method').selectedIndex = 0;
        paymentReceivedInput.value = '';
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Checkout error:', error);
    }
}

function showReceipt(receipt) {
    const receiptDate = new Date(receipt.dateTime);
    const formattedDate = receiptDate.toLocaleDateString() + ' ' + receiptDate.toLocaleTimeString();

    let itemsHtml = '';
    receipt.items.forEach(item => {
        itemsHtml += `
            <div class="receipt-item">
                <div>${item.productName}</div>
                <div>${item.quantity} x $${parseFloat(item.price).toFixed(2)}</div>
                <div>$${(item.price * item.quantity).toFixed(2)}</div>
            </div>
        `;
    });

    receiptContent.innerHTML = `
        <div class="receipt-preview">
            <div class="receipt-header">
                <h2>Store Receipt</h2>
                <div class="receipt-id">Receipt #${receipt.id}</div>
                <div class="receipt-date">${formattedDate}</div>
                <div class="receipt-cashier">Cashier: ${receipt.cashierName}</div>
                <div class="receipt-payment">Payment: ${receipt.paymentMethod}</div>
                <div class="receipt-received">Received: $${receipt.paymentReceived.toFixed(2)}</div>
                <div class="receipt-change">Change: $${receipt.change.toFixed(2)}</div>
            </div>
            <div class="receipt-items">
                ${itemsHtml}
            </div>
            <div class="receipt-total">
                Subtotal: $${(receipt.total - receipt.taxAmount).toFixed(2)}<br>
                Tax (10%): $${receipt.taxAmount.toFixed(2)}<br>
                Total: $${receipt.total.toFixed(2)}
            </div>
            <div class="receipt-footer">
                Thank you for shopping with us!
            </div>
        </div>
    `;

    if (receiptModal) {
        receiptModal.style.display = 'block';
        receiptModal.classList.add('show');
    } else {
        console.error('Receipt modal element not found');
        showToast('Could not display receipt', 'error');
    }
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
        .receipt-total-row { display: flex; justify-content: space-between; }
        .receipt-footer { margin-top: 20px; text-align: center; }
    `);
    printWindow.document.write('</style></head><body>');
    printWindow.document.write(receiptContent.innerHTML);
    printWindow.document.write('</body></html>');
    printWindow.document.close();

    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
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
    fetchProducts();

    checkoutBtn.addEventListener('click', processCheckout);
    clearCartBtn.addEventListener('click', clearCart);

    if (receiptModal && receiptModalCloseBtn) {
        receiptModalCloseBtn.addEventListener('click', () => {
            receiptModal.style.display = 'none';
            receiptModal.classList.remove('show');
        });

        receiptModal.addEventListener('click', (event) => {
            if (event.target === receiptModal) {
                receiptModal.style.display = 'none';
                receiptModal.classList.remove('show');
            }
        });
    }


    if (printReceiptBtn) {
        printReceiptBtn.addEventListener('click', printReceipt);
    }
});
