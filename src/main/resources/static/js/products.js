const productForm = document.getElementById('product-form');
const productIdInput = document.getElementById('product-id');
const productNameInput = document.getElementById('product-name');
const productDescriptionInput = document.getElementById('product-description');
const productPriceInput = document.getElementById('product-price');
const productCategoryInput = document.getElementById('product-category');
const productBarcodeInput = document.getElementById('product-barcode');
const productStockInput = document.getElementById('product-stock');
const productTable = document.getElementById('products-table');
const productFormTitle = document.getElementById('product-form-title');
const cancelEditBtn = document.getElementById('cancel-edit-btn');

let isEditing = false;
let products = [];

async function fetchProducts() {
    try {
        const response = await fetch('/products/api/all');
        if (!response.ok) {
            throw new Error('Failed to fetch products');
        }
        products = await response.json();
        renderProductsTable();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}

function renderProductsTable() {
    const tableBody = productTable.querySelector('tbody');
    tableBody.innerHTML = '';

    if (products.length === 0) {
        const emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="8" class="text-center">No products found. Add some products to get started.</td>';
        tableBody.appendChild(emptyRow);
        return;
    }

    products.forEach(product => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${product.name}</td>
            <td>${product.description || '-'}</td>
            <td>$${parseFloat(product.price).toFixed(2)}</td>
            <td>${product.category || '-'}</td>
            <td>${product.barcode || '-'}</td>
            <td>${product.stockQuantity}</td>
            <td>${product.productType || '-'}</td>
            <td>
              <div class="action-buttons">
                <button class="btn btn-sm btn-secondary edit-btn" data-id="${product.id}">Edit</button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${product.id}">Delete</button>
              </div>
            </td>

        `;

        tableBody.appendChild(row);
    });

    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => editProduct(btn.dataset.id));
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => deleteProduct(btn.dataset.id));
    });
}

const productTypeInput = document.getElementById('product-type');

async function submitProductForm(event) {
    event.preventDefault();

    const productData = {
        name: productNameInput.value.trim(),
        description: productDescriptionInput.value.trim(),
        price: parseFloat(productPriceInput.value),
        category: productCategoryInput.value.trim(),
        barcode: productBarcodeInput.value.trim(),
        stockQuantity: parseInt(productStockInput.value, 10),
        productType: productTypeInput.value
    };

    if (!productData.name) {
        showToast('Product name is required', 'error');
        return;
    }
    if (isNaN(productData.price) || productData.price <= 0) {
        showToast('Valid price is required', 'error');
        return;
    }
    if (isNaN(productData.stockQuantity) || productData.stockQuantity < 0) {
        showToast('Valid stock quantity is required', 'error');
        return;
    }

    try {
        let url = '/products/api';
        let method = 'POST';

        if (isEditing) {
            productData.id = productIdInput.value;
            url = `/products/api/${productData.id}`;
            method = 'PUT';
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(productData)
        });

        if (!response.ok) {
            throw new Error('Failed to save product');
        }

        const savedProduct = await response.json();
        if (isEditing) {
            showToast('Product updated successfully');
        } else {
            showToast('Product added successfully');
        }

        resetForm();
        fetchProducts();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
    }
}


function editProduct(productId) {
    productId = Number(productId);
    const product = products.find(p => p.id === productId);

    if (product) {
        productIdInput.value = product.id;
        productNameInput.value = product.name;
        productDescriptionInput.value = product.description || '';
        productPriceInput.value = product.price;
        productCategoryInput.value = product.category || '';
        productBarcodeInput.value = product.barcode || '';
        productStockInput.value = product.stockQuantity;

        productFormTitle.textContent = 'Edit Product';
        isEditing = true;
        cancelEditBtn.style.display = 'inline-block';
        productNameInput.focus();

        productForm.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } else {
        showToast('Product not found', 'error');
    }
}

async function deleteProduct(productId) {
    productId = Number(productId);
    
    if (!confirm('Are you sure you want to delete this product?')) {
        return;
    }

    try {
        const response = await fetch(`/products/api/${productId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to delete product');
        }

        showToast('Product deleted successfully', 'success');
        fetchProducts();
    } catch (error) {
        showToast('Error: ' + error.message, 'error');
        console.error('Delete error:', error);
    }
}

function resetForm() {
    productForm.reset();
    productIdInput.value = '';
    isEditing = false;
    productFormTitle.textContent = 'Add New Product';
    cancelEditBtn.style.display = 'none';
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

    productForm.addEventListener('submit', submitProductForm);
    cancelEditBtn.addEventListener('click', (event) => {
        event.preventDefault();
        resetForm();
    });
});
