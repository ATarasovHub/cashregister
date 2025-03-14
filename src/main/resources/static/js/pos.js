// POS System JavaScript

// DOM Elements
const productsGrid = document.getElementById('products-grid');
const cartItemsContainer = document.getElementById('cart-items');
const subtotalElement = document.getElementById('subtotal');
const taxElement = document.getElementById('tax');
const totalElement = document.getElementById('total');
const checkoutBtn = document.getElementById('checkout-btn');
const clearCartBtn = document.getElementById('clear-cart-btn');
const cashierNameInput = document.getElementById('cashier-name');
const paymentMethodSelect = document.getElementById('payment-method');
const receiptModalBackdrop = document.getElementById('receipt-modal-backdrop');
const receiptModalCloseBtn = document.getElementById('receipt-modal-close');
const printReceiptBtn = document.getElementById('print-receipt-btn');
const receiptContent = document.getElementById('receipt-content');

// State Management
let products = [];
let cartItems = [];
const TAX_RATE = 0.10; // 10% tax rate

// Fetch Products
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

// Render Products
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

// Add Product to Cart
function addToCart(product) {
    if (product.stockQuantity <= 0) {
        showToast('Product is out of stock', 'error');
        return;
    }

    const existingItem = cartItems.find(item => item.productId === product.id);

    if (existingItem) {
        if (existingItem.quantity < product.stockQuantity) {
            existingItem.quantity += 1;
        } else {
            showToast('Cannot add more of this product - stock limit reached', 'warning');
            return;
        }
    } else {
        cartItems.push({
            productId: product.id,
            productName: product.name,
            price: product.price,
            quantity: 1,
            maxQuantity: product.stockQuantity
        });
    }

    renderCart();
    showToast('Product added to cart');
}

// Update Cart Item Quantity
function updateCartItemQuantity(productId, change) {
    const itemIndex = cartItems.findIndex(item => item.productId === productId);

    if (itemIndex !== -1) {
        const newQuantity = cartItems[itemIndex].quantity + change;

        if (newQuantity > 0 && newQuantity <= cartItems[itemIndex].maxQuantity) {
            cartItems[itemIndex].quantity = newQuantity;
        } else if (newQuantity <= 0) {
            cartItems.splice(itemIndex, 1);
        } else {
            showToast('Cannot add more of this product - stock limit reached', 'warning');
            return;
        }

        renderCart();
    }
}

// Remove Item from Cart
function removeCartItem(productId) {
    cartItems = cartItems.filter(item => item.productId !== productId);
    renderCart();
}

// Render Cart
function renderCart() {
    cartItemsContainer.innerHTML = '';

    if (cartItems.length === 0) {
        cartItemsContainer.innerHTML = '<div class="empty-cart">Cart is empty</div>';
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
            <div class="item-details">
                <div class="item-title">${item.productName}</div>
                <div class="item-price">$${parseFloat(item.price).toFixed(2)} x ${item.quantity}</div>
            </div>
            <div class="item-quantity">
                <button class="quantity-btn decrease" data-id="${item.productId}">-</button>
                <span class="quantity-value">${item.quantity}</span>
                <button class="quantity-btn increase" data-id="${item.productId}">+</button>
            </div>
            <div class="item-total">$${itemTotal.toFixed(2)}</div>
            <button class="btn-delete" data-id="${item.productId}">×</button>
        `;

        cartItemsContainer.appendChild(cartItemElement);
    });

    // Add event listeners for quantity buttons
    document.querySelectorAll('.quantity-btn.decrease').forEach(btn => {
        btn.addEventListener('click', () => updateCartItemQuantity(btn.dataset.id, -1));
    });

    document.querySelectorAll('.quantity-btn.increase').forEach(btn => {
        btn.addEventListener('click', () => updateCartItemQuantity(btn.dataset.id, 1));
    });

    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', () => removeCartItem(btn.dataset.id));
    });

    // Calculate totals
    const tax = subtotal * TAX_RATE;
    const total = subtotal + tax;

    subtotalElement.textContent = `$${subtotal.toFixed(2)}`;
    taxElement.textContent = `$${tax.toFixed(2)}`;
    totalElement.textContent = `$${total.toFixed(2)}`;

    enableCheckout();
}

// Enable/Disable Checkout Button
function enableCheckout() {
    checkoutBtn.disabled = false;
}

function disableCheckout() {
    checkoutBtn.disabled = true;
}

// Clear Cart
function clearCart() {
    cartItems = [];
    renderCart();
}

// Process Checkout
async function processCheckout() {
    if (cartItems.length === 0) {
        showToast('Cart is empty', 'warning');
        return;
    }

    const cashierName = cashierNameInput.value.trim();
    if (!cashierName) {
        showToast('Please enter cashier name', 'warning');
        return;
    }

    const receiptData = {
        cashierName: cashierName,
        paymentMethod: paymentMethodSelect.value,
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
        showReceipt(receipt);
        clearCart();
        fetchProducts(); // Refresh products to get updated stock
        showToast('Checkout successful!');
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

// Show Receipt
function showReceipt(receipt) {
    // Format the receipt date
    const receiptDate = new Date(receipt.dateTime);
    const formattedDate = receiptDate.toLocaleDateString() + ' ' + receiptDate.toLocaleTimeString();

    // Generate receipt items HTML
    let itemsHtml = '';
    receipt.items.forEach(item => {
        itemsHtml += `
            <div class="receipt-item">
                <div class="item-name">${item.productName}</div>
                <div class="item-quantity">${item.quantity}</div>
                <div class="item-price">$${parseFloat(item.price).toFixed(2)}</div>
                <div class="item-total">$${parseFloat(item.total).toFixed(2)}</div>
            </div>
        `;
    });

    // Generate receipt HTML
    receiptContent.innerHTML = `
        <div class="receipt-preview">
            <div class="receipt-header">
                <h2>POS System Receipt</h2>
                <div class="receipt-id">Receipt #${receipt.id}</div>
                <div class="receipt-date">${formattedDate}</div>
                <div class="receipt-cashier">Cashier: ${receipt.cashierName}</div>
                <div class="receipt-payment">Payment: ${receipt.paymentMethod}</div>
            </div>
            <div class="receipt-items">
                <div class="receipt-item receipt-header-row">
                    <div class="item-name"><strong>Item</strong></div>
                    <div class="item-quantity"><strong>Qty</strong></div>
                    <div class="item-price"><strong>Price</strong></div>
                    <div class="item-total"><strong>Total</strong></div>
                </div>
                ${itemsHtml}
            </div>
            <div class="receipt-total">
                <div class="receipt-total-row">
                    <div>Subtotal:</div>
                    <div>$${(parseFloat(receipt.total) - parseFloat(receipt.taxAmount)).toFixed(2)}</div>
                </div>
                <div class="receipt-total-row">
                    <div>Tax (10%):</div>
                    <div>$${parseFloat(receipt.taxAmount).toFixed(2)}</div>
                </div>
                <div class="receipt-total-row total-amount">
                    <div><strong>Total:</strong></div>
                    <div><strong>$${parseFloat(receipt.total).toFixed(2)}</strong></div>
                </div>
            </div>
            <div class="receipt-footer">
                Thank you for your purchase!
            </div>
        </div>
    `;

    // Show the modal
    receiptModalBackdrop.classList.add('show');
}

// Print Receipt
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

    // Wait for the content to load and then print
    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
}

// Toast Notification
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // Trigger reflow
    toast.offsetHeight;

    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 3000);
}

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    fetchProducts();

    checkoutBtn.addEventListener('click', processCheckout);
    clearCartBtn.addEventListener('click', clearCart);

    receiptModalCloseBtn.addEventListener('click', () => {
        receiptModalBackdrop.classList.remove('show');
    });

    printReceiptBtn.addEventListener('click', printReceipt);

    // Close modal when clicking outside
    receiptModalBackdrop.addEventListener('click', (event) => {
        if (event.target === receiptModalBackdrop) {
            receiptModalBackdrop.classList.remove('show');
        }
    });
});
