/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
const randomPrices = [];
cartItems = [];
function addToCart(movieIndex, movieTitle, moviePrice) {
    // Check if the movie is already in the cart
    const existingCartItem = cartItems.find((item) => item.title === movieTitle);

    if (existingCartItem) {
        // // If the movie is already in the cart, update its quantity
        existingCartItem.quantity++;
        // // Update the cart
        updateQuantityOnServer(existingCartItem.title, existingCartItem.quantity);
        //incrementQuantity(movieIndex);
    } else {
        // If the movie is not in the cart, add a new item with quantity 1
        cartItems.push({ title: movieTitle, price: moviePrice, quantity: 1 });
        jQuery.ajax({
            dataType: "json",
            method: "POST",
            url: "api/cart", // Replace with the actual URL to your servlet
            data: JSON.stringify({ title: movieTitle, price: moviePrice, quantity: 1 }),
            contentType: "application/json; charset=utf-8", // Set the request content type
            success: function () {
                console.log("Item is added to the cart on the server");
            },
            error: function (error) {
                console.error("Error adding item to the cart", error);
            }
        });
    }

    updateCart();
    console.log("Updated cart:", cartItems);
}
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");
    let add = $('cart');

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        // Generate a random price between 5 and 50 (adjust the range as needed)
        const randomPrice = (Math.random() * (50 - 5) + 5).toFixed(2);

        // Store the random price in the array
        randomPrices.push(randomPrice);

        // Concatenate the html tags with resultData jsonObject
        starIdArray = resultData[i]["starIds"].split(',');
        starNameArray = resultData[i]["star"].split(',');
        genresArr = resultData[i]["genres"].split(',');
        genresIdArr = resultData[i]["genresId"].split(',');
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter. href="single-star.html?id='
            '<a href="single-movie.html?price=' + randomPrice + '&id=' + resultData[i]['id'] + '">'
            + resultData[i]["title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < genresArr.length; j++) {
            rowHTML +=
                '<a href="#" onclick="handleGenre(' + genresIdArr[j] + ')">' +
                genresArr[j] +
                '</a>' + ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>";

        for (let j = 0; j < starNameArray.length; j++) {
            rowHTML +=
                // Add a link to single-star.html with id passed with GET url parameter. href="single-star.html?id='
                '<a href="single-star.html?starId=' + starIdArray[j] + '">'
                + starNameArray[j] +
                '</a>' + ", ";
        }
        // Add the random price to the row as well as the add button
        rowHTML += "<th>$" + randomPrice + "</th>";
        rowHTML += '<th><button onclick="addToCart(' + i + ', \'' + resultData[i]["title"] + '\', ' + randomPrice + ')" type="button" class="btn btn-primary">Add To Shopping Cart</button></th>';
        // rowHTML += "<th><button class=\"btn btn-primary\"> ADD TO CART</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);

    }
}

function goToShoppingCart() {
    // Serialize the cart data as JSON
    const cartData = JSON.stringify(cartItems);

    // Navigate to the shopping cart page with cart data as a query parameter
    window.location.href = `shopping-cart.html?cartData=${encodeURIComponent(cartData)}`;
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
// function define but not call yet until
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

function handleGenre(genresId){
    if(genresId) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/single-genre?genreId=" + genresId,
            success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }

}

