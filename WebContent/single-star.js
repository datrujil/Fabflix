/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function addToCart(movieIndex, movieTitle, moviePrice) {
    alert("Item is added");
    // Check if the movie is already in the cart
    const existingCartItem = cartItems.find((item) => item.title === movieTitle);

    if (existingCartItem) {
        // If the movie is already in the cart, update its quantity
        existingCartItem.quantity++;
        // Update the cart
        updateQuantityOnServer(existingCartItem.title, existingCartItem.quantity);
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

function goToShoppingCart() {
    // Serialize the cart data as JSON
    const cartData = JSON.stringify(cartItems);

    // Navigate to the shopping cart page with cart data as a query parameter
    window.location.href = `shopping-cart.html?cartData=${encodeURIComponent(cartData)}`;
}
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    let price = getParameterByName("price");

    console.log("handleResult: populating movie table from resultData");

    let starInfoElement = jQuery("#star_info");

    if (!resultData[0]["birthYear"]){
        birthYear = "N/A";
    }
    else{
        birthYear = resultData[0]["birthYear"];
    }

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["starName"] + "</p>" +
        "<p>Date Of Birth: " + birthYear + "</p>");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter. href="single-movie.html?id='
            '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">'
            + resultData[i]["title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        // rowHTML += "<th>" + resultData[i]["starName"] + "</th>";

        // rowHTML += "<th><button class=\"btn btn-primary\"> ADD TO CART</button></th>";
        rowHTML += '<th><button onclick="addToCart(' + i + ', \'' + resultData[i]["title"] + '\', ' + price + ')" type="button" class="btn btn-primary">Add To Shopping Cart</button></th>';

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('starId');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});