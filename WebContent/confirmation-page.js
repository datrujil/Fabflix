let cartData = getParameterByName("cartData");
let customerId = getParameterByName("id"); // Retrieve the "id" parameter from the URL

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

function handleSaleResult(resultData){
    console.log("handleSaleResult: populating sale table from resultData");

    // Populate the star table
    // Find the empty table body by id "sale_table_body"
    let saleTableBodyElement = jQuery("#sale_table_body");
    let add = $('sale');
    let cartItems = JSON.parse(cartData);
    let total = 0;

    resultData.forEach((item) => {
        for (let i = 0; i < cartItems.length; i++){
            if (item["customerId"] == customerId && item["title"] == cartItems[i]["title"]){
                let movieTitle = item["title"];
                let quantity = cartItems[i]["quantity"];
                let price = cartItems[i]["price"];
                let rowHTML = "<tr>";
                rowHTML += "<th>" + item["id"] + "</th>";
                rowHTML += "<th>" + item["saleDate"] + "</th>";
                rowHTML += "<th>" + item["title"] + "</th>";
                rowHTML += "<th>" + quantity + "</th>";
                rowHTML += "<th>$" + price + "</th>";
                rowHTML += "<th>";
                rowHTML += "</tr>";

                // Append the row created to the table body
                total += (price * quantity);
                let saleTableBodyElement = jQuery("#sale_table_body");
                saleTableBodyElement.append(rowHTML);
            }
        }
    });
    const totalPriceElement = document.querySelector('.overall-price');
    if (totalPriceElement) totalPriceElement.textContent = `$${total.toFixed(2)}`;
}

// Function to find the movie title by movie ID
function findMovieTitleByMovieId(cartItems, movieId) {
    for (let i = 0; i < cartItems.length; i++) {
        if (cartItems[i].id === movieId) {
            return cartItems[i].title;
        }
    }
    return "N/A";
}

// Function to find the movie title by movie ID
function findMovieQuantityByMovieTitle(cartItems, movieTitle) {
    for (let i = 0; i < cartItems.length; i++) {
        if (cartItems[i]["title"] === movieTitle) {
            return cartItems[i]["quantity"];
        }
    }
    return "N/A";
}

function findMoviePriceByMovieTitle(cartItems, movieTitle) {
    for (let i = 0; i < cartItems.length; i++) {
        if (cartItems[i]["title"] === movieTitle) {
            return cartItems[i]["price"];
        }
    }
    return "N/A";
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `api/sales?customerId=${customerId}&cartData=${encodeURIComponent(cartData)}`,
    success: (resultData) => handleSaleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

//TO DO: ADD TOTAL TO BOTTOM OF PAGE