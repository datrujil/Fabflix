

// Function to generate a cart item
let cartItems = [];
function createCartItem(item, index) {
    const cartItem = document.createElement('div');
    cartItem.classList.add('cart-item');
    cartItem.innerHTML = `
    <div class="card rounded-3 mb-4">
        <div class="card-body p-4">
            <div class="row align-items-center">
                <div class="col-md-2 col-lg-2 col-xl-2">
                    <div class="movie-title">${item.title}</div>
                </div>
                <div class="quantity">
                <div class="col-md-3 col-lg-3 col-xl-3">
                    <div class="d-flex align-items-center">
                        <button class="decrement" data-index="${index}"><i class="fas fa-minus"></i></button>
                        <input type="number" value="${item.quantity}" min="1" data-index="${index}">
                        <button class="increment" data-index="${index}"><i class="fas fa-plus"></i></button>
                    </div>
                </div>
                </div>
                <div class="col-md-3 col-lg-2 col-xl-2">
                    <div class="price">$${item.price.toFixed(2)}</div>
                </div>
                <div class="col-md-2 col-lg-2 col-xl-2 text-center">
                    <div class="total-price" data-index="${index}">$${(item.price * item.quantity).toFixed(2)}</div>
                </div>
                <div class="col-md-2 col-lg-2 col-xl-2 text-right">
                    <button class="delete" data-index="${index}"><i class="fas fa-trash fa-lg"></i></button>
                </div>
            </div>
        </div>
    </div>
    `;

    return cartItem;
}

// Function to calculate and update the total price
// Function to calculate and update the total price
function updateTotalPrice() {
    const total = cartItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
    const totalPriceElement = document.querySelector('.overall-price');
    if (totalPriceElement) totalPriceElement.textContent = `$${total.toFixed(2)}`;
}

// Function to increment the quantity of an item
function incrementQuantity(index) {
    cartItems[index].quantity++;
    updateQuantityOnServer(cartItems[index].title, cartItems[index].quantity);
    updateTotalPrice();
    updateCart();
}

// Function to decrement the quantity of an item
function decrementQuantity(index) {
    if (cartItems[index].quantity > 1) {
        cartItems[index].quantity--;
        updateQuantityOnServer(cartItems[index].title, cartItems[index].quantity);
        updateTotalPrice();
        updateCart();
    }
}

function deleteCartItem(index) {
    deleteCartItemOnServer(cartItems[index].title);
    updateTotalPrice();
    updateCart();
}

// Function to handle changes in quantity and delete items
function updateCart() {
    const cart = document.querySelector('.cart');
    cart.innerHTML = ''; // Clear the cart

    cartItems.forEach((item, index) => {
        const cartItem = createCartItem(item, index);
        cart.appendChild(cartItem);

        const incrementBtn = cartItem.querySelector('.increment');
        const decrementBtn = cartItem.querySelector('.decrement');
        const deleteBtn = cartItem.querySelector('.delete');

        incrementBtn.addEventListener('click', () => {
            incrementQuantity(index);
        });

        decrementBtn.addEventListener('click', () => {
            decrementQuantity(index);
        });

        deleteBtn.addEventListener('click', () => {
            deleteCartItem(index)
        });
    });

    updateTotalPrice();
}



function updateQuantityOnServer(movieTitle, quantity) {
    // Send a request to the server to update the quantity of the specified movie
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/cart",
        data: JSON.stringify({ title: movieTitle, quantity: quantity }),
        contentType: "application/json; charset=utf-8", // Set the request content type
        success: function (response) {
            // Handle success, maybe update the UI to reflect the change
            console.log("Quantity updated on the server:", response);
        },
        error: function (error) {
            console.error("Error updating quantity on the server", error);
        }
    });
}

function deleteCartItemOnServer(movieTitle) {
    // Send a DELETE request to the server to delete the specified movie from the cart
    fetch(`api/cart?title=${encodeURIComponent(movieTitle)}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.status === 200) {
                // Success, item deleted on the server
                return response.json(); // Assuming the server responds with updated cart data
            } else {
                // Handle errors if needed
                throw new Error('Delete request failed');
            }
        })
        .then(updatedCartData => {
            // Update the client's cartItems with the updated data
            cartItems = updatedCartData;

            // Update the cart UI and total price
            updateCart();
            updateTotalPrice();
        })
        .catch(error => {
            console.error('Error deleting cart item on the server:', error);
        });
}

function goToPaymentPage(){
    $.ajax({
        type: "GET",
        url: "api/cart?action=getData",
        dataType: "json",
        success: function (userData) {
            if (userData.status === "success") {
                // User data is available in userData.userId, userData.username, etc.
                const cartData = JSON.stringify(cartItems);
                window.location.href = `payment-page.html?id=${userData.userId}&cartData=${encodeURIComponent(cartData)}`;
            } else {
                console.error("Error fetching user");
            }
        },
        error: function (error) {
            console.error("Error fetching user data", error);
        }
    });
}

// Function to load and display the cart contents on page load
function loadCartContents() {
    jQuery.ajax({
        method: "GET",
        url: "api/cart", // Replace with the actual API endpoint for fetching cart data
        dataType: "json",
        success: function (cartData) {
            const consolidatedCartItems = {};

            cartData.forEach((item) => {
                if (!consolidatedCartItems[item.title]) {
                    consolidatedCartItems[item.title] = {...item};
                } else {
                    consolidatedCartItems[item.title].quantity += item.quantity;
                }
            });

            // Update cartItems with the consolidated data
            cartItems = Object.values(consolidatedCartItems);

            updateCart(); // Update the cart UI with the loaded data
        },
        error: function (error) {
            console.error("Error loading cart contents", error);
        },
    });
}

// Call the loadCartContents function when the page loads
$(document).ready(function () {
    loadCartContents();
});