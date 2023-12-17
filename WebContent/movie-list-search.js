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

// DEFAULT PAGINATION INFO
let currentPage = 1; // Initialize current page
let itemsPerPage = 10; // Initialize items per page
let totalPages = null;

// Function to update pagination information
function updatePaginationInfo(totalPages) {
    const paginationInfo = `Page ${currentPage} of ${totalPages}`;
    //document.getElementById('pagination-info').textContent = paginationInfo;
}
// Function to handle the AJAX request with pagination
function handlePaginationResult(resultData) {
    // Update the table with the data from the server (e.g., handleResult(resultData))

    // You should receive the total number of pages from the server response
    const totalPages = resultData.totalPages;

    // Update the pagination information
    //updatePaginationInfo(totalPages);
}

function goToShoppingCart() {
    // Serialize the cart data as JSON
    const cartData = JSON.stringify(cartItems);

    // Navigate to the shopping cart page with cart data as a query parameter
    window.location.href = `shopping-cart.html?cartData=${encodeURIComponent(cartData)}`;
}

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
    console.log("result:", results);
    console.log("result:[2]",results[2]);
    console.log("decode:", decodeURIComponent(results[2].replace(/\+/g, " ")));


    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");
    movieTableBodyElement.empty();
    let add = $('cart');

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {
        // Generate a random price between 5 and 50 (adjust the range as needed)
        randomPrice = (Math.random() * (50 - 5) + 5).toFixed(2);

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
            if (starNameArray[j] != ""){
                rowHTML +=
                    // Add a link to single-star.html with id passed with GET url parameter. href="single-star.html?id='
                    '<a href="single-star.html?starId=' + starIdArray[j] + '">'
                    + starNameArray[j] +
                    '</a>' + ", ";
            }
        }
        // Add the random price to the row as well as the add button
        rowHTML += "<th>$" + randomPrice + "</th>";
        rowHTML += '<th><button onclick="addToCart(' + i + ', \'' + resultData[i]["title"] + '\', ' + randomPrice + ')" type="button" class="btn btn-primary">Add To Shopping Cart</button></th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);

        // You should receive the total number of pages from the server response
        totalPages = resultData.totalPages;

        // Update the pagination information
        //updatePaginationInfo(totalPages);
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

let genresId = getParameterByName('genreId');
let startWith = getParameterByName('startWith');
let byTitle = getParameterByName('byTitle');
let byYear = getParameterByName('byYear');
let byDirector = getParameterByName('byDirector');
let byStar = getParameterByName('byStar');

// Define the URL and query parameters
const apiUrl = 'api/single-genre';
const queryParams = [];

if (byTitle != null) {
    queryParams.push(`byTitle=${byTitle}`);
}

if (byYear != null) {
    queryParams.push(`byYear=${byYear}`);
}

if (byDirector != null) {
    queryParams.push(`byDirector=${byDirector}`);
}

if (byStar != null) {
    queryParams.push(`byStar=${byStar}`);
}

// Check if any query parameters are specified
if (queryParams.length > 0) {
    queryParams.push(`page=${currentPage}`);
    queryParams.push(`pageSize=${itemsPerPage}`);
    const queryString = queryParams.join('&');
    const fullUrl = `${apiUrl}?${queryString}`;

    // Make an AJAX request
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: fullUrl,
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully
    });
}

// Makes the HTTP GET request and registers on success callback function handleStarResult
// function define but not call yet until
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

handleGenre(genresId);
// search by char
if(startWith) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/single-genre?startWith=" + startWith,
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function handleSort(event)
{
    // event.preventDefault();
    let firstChoice = document.getElementById('first-choice').value;
    let firstPriorityType = document.getElementById('first-priority-type').value;
    let secondChoice = document.getElementById('second-choice').value;
    let secondPriorityType = document.getElementById('second-priority-type').value;
    var itemsPerPageSelect = document.getElementById("items-per-page");
    var currentPageInput = document.getElementById("current-page");
    // Get the current selected value
    var currentItemsPerPage = itemsPerPageSelect.value;
    var currentPage = currentPageInput.value;

    console.log("current Items per page: ", currentItemsPerPage)
    console.log("firstChoice:", firstChoice);
    console.log("secondChoice:", secondChoice);
    console.log("firstType:", firstPriorityType);
    console.log("second Type:", secondPriorityType);

    if (getParameterByName('genreId') == null)
        genresId = '';
    if(getParameterByName('startWith') == null)
        startWith = '';
    if(getParameterByName('byTitle') == null)
        byTitle = '';
   if (getParameterByName('byYear') == null)
       byYear = '';
    if(getParameterByName('byDirector') == null)
        byDirector = '';
    if (getParameterByName('byStar') == null)
        byStar = '';


    if (firstChoice === secondChoice){
        return alert("conflict sort choice");
    }

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: `${apiUrl}?genreId=${genresId}&startWith=${startWith}&byTitle=${byTitle}&byYear=${byYear}&byDirector=${byDirector}&byStar=${byStar}&firstChoice=${firstChoice}&firstPriorityType=${firstPriorityType}&secondChoice=${secondChoice}&secondPriorityType=${secondPriorityType}&page=${currentPage}&pageSize=${currentItemsPerPage}`,
        success: (resultData) => handleResult(resultData)
    });

}

function goToPreviousPage(){
    const firstChoice = document.getElementById("first-choice").value;
    const firstPriorityType = document.getElementById("first-priority-type").value;
    const secondChoice = document.getElementById("second-choice").value;
    const secondPriorityType = document.getElementById("second-priority-type").value;
    let currentItemsPerPageInput = document.getElementById("items-per-page");
    var currentItemsPerPage = currentItemsPerPageInput.value;
    let currentPageInput = document.getElementById("current-page");
    var currentPage = currentPageInput.value;

    // if (currentItemsPerPage != itemsPerPage)
    // {
    //     itemsPerPage = currentItemsPerPage;
    // }

    if (currentPage > 1){
        currentPage--;
        currentPageInput.value = currentPage.toString();
        currentPage = currentPageInput.value;

        const queryParams = [];
        if (getParameterByName('genreId') == null)
            genresId = '';
        if(getParameterByName('startWith') == null)
            startWith = '';
        if(getParameterByName('byTitle') == null)
            byTitle = '';
        if (getParameterByName('byYear') == null)
            byYear = '';
        if(getParameterByName('byDirector') == null)
            byDirector = '';
        if (getParameterByName('byStar') == null)
            byStar = '';
        queryParams.push(`page=${currentPage}`);
        queryParams.push(`pageSize=${currentItemsPerPage}`);
        const queryString = queryParams.join('&');
        // Send sorting criteria to the servlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: `${apiUrl}?${queryString}&genreId=${genresId}&startWith=${startWith}&byTitle=${byTitle}&byYear=${byYear}&byDirector=${byDirector}&byStar=${byStar}&firstChoice=${firstChoice}&firstPriorityType=${firstPriorityType}&secondChoice=${secondChoice}&secondPriorityType=${secondPriorityType}&page=${currentPage}&pageSize=${currentItemsPerPage}`,
            success: function (resultData) {
                // Handle the response data (e.g., update the table with sorted data)
                handleResult(resultData);
            },
            error: function (error) {
                console.error("Error during sorting", error);
            }
        });
    }
}

function goToNextPage(){
    const firstChoice = document.getElementById("first-choice").value;
    const firstPriorityType = document.getElementById("first-priority-type").value;
    const secondChoice = document.getElementById("second-choice").value;
    const secondPriorityType = document.getElementById("second-priority-type").value;
    let currentItemsPerPageInput = document.getElementById("items-per-page");
    var currentItemsPerPage = currentItemsPerPageInput.value;
    let currentPageInput = document.getElementById("current-page");
    var currentPage = currentPageInput.value;

    if (currentPage < 100){
        currentPage++;

        const queryParams = [];
        if (getParameterByName('genreId') == null)
            genresId = '';
        if(getParameterByName('startWith') == null)
            startWith = '';
        if(getParameterByName('byTitle') == null)
            byTitle = '';
        if (getParameterByName('byYear') == null)
            byYear = '';
        if(getParameterByName('byDirector') == null)
            byDirector = '';
        if (getParameterByName('byStar') == null)
            byStar = '';
        queryParams.push(`page=${currentPage}`);
        queryParams.push(`pageSize=${currentItemsPerPage}`);
        const queryString = queryParams.join('&');
        // Send sorting criteria to the servlet
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: `${apiUrl}?${queryString}&genreId=${genresId}&startWith=${startWith}&byTitle=${byTitle}&byYear=${byYear}&byDirector=${byDirector}&byStar=${byStar}&firstChoice=${firstChoice}&firstPriorityType=${firstPriorityType}&secondChoice=${secondChoice}&secondPriorityType=${secondPriorityType}&page=${currentPage}&pageSize=${currentItemsPerPage}`,
            success: function (resultData) {
                console.log("json len:", resultData.length);
                console.log("itemperpage:", itemsPerPage);
                console.log("current page: ", currentPage);
                if(resultData.length == 0)
                {
                    currentPage--;
                    alert("End of page");
                    return;

                }
                else
                {
                    currentPageInput.value = currentPage.toString();
                    currentPage = currentPageInput.value;
                    handleResult(resultData);
                }
            },
            error: function (error) {
                console.error("Error during sorting", error);
            }
        });
    }
}
