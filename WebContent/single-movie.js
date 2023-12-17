
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

function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    let price = getParameterByName("price");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {
        if (resultData[i]["starIds"] != null){
            starIdArray = resultData[i]["starIds"].split(',');
        } else { starIdArray = []; }

        if (resultData[i]["star"] != null){
            starNameArray = resultData[i]["star"].split(',');
        } else { starNameArray = []; }

        if (resultData[i]["genres"] != null){
            genresArr = resultData[i]["genres"].split(',');
        } else { genresArr = []; }

        if (resultData[i]["genresId"] != null){
            genresIdArr = resultData[i]["genresId"].split(',');
        }
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter. href="single-star.html?id='
            '<a href="single-movie.html?price=' + price + '&id=' + resultData[i]['id'] + '">'
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
        rowHTML += "</th>";
        rowHTML += '<th><button onclick="addToCart(' + i + ', \'' + resultData[i]["title"] + '\', ' + price + ')" type="button" class="btn btn-primary">Add To Shopping Cart</button></th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
let movieId = getParameterByName('id');
// function define but not call yet until
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
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