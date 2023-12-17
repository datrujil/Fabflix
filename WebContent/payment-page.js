function checkCustomerPaymentInfo(resultData){

    // Obtain user input from the form
    const firstName = $("#fname").val();
    const lastName = $("#lname").val();
    const creditCardNumber = $("#ccnum").val();
    const expirationMonth = $("#expmonth").val();
    const expirationYear = $("#expyear").val();
    const expirationDay = $("#expday").val();

    // Create a data object to send to the server
    const paymentData = {
        firstName: firstName,
        lastName: lastName,
        creditCardNumber: creditCardNumber,
        expirationMonth: expirationMonth,
        expirationYear: expirationYear,
        expirationDay: expirationDay
    };

    // Send the payment data to the server using AJAX
    $.ajax({
        dataType: "json",
        method: "POST",
        url: "api/payment", // Replace with the actual URL for payment processing
        data: JSON.stringify(paymentData),
        contentType: "application/json; charset=utf-8",
        success: function(data){
            if(data){
                submitSalesData();
                let id = customerId;
                window.location.href = `confirmation-page.html?id=${id}&cartData=${encodeURIComponent(cartData)}`;
            }
        },
        error: function(xhr, status, error) {
            alert("Payment Information Not Found.");
        }
    });

// Now you have the user's input in the JavaScript variables

// For example, you can log the input data
    console.log("First Name: " + firstName);
    console.log("Last Name: " + lastName);
    console.log("Credit Card Number: " + creditCardNumber);
    console.log("Expiration Month: " + expirationMonth);
    console.log("Expiration Year: " + expirationYear);
    console.log("Expiration Day: " + expirationDay);
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

function submitSalesData() {
    try {
        let cartItems = JSON.parse(cartData);

        // Loop through the cartItems array
        for (let i = 0; i < cartItems.length; i++) {
            const movieTitle = cartItems[i]["title"];
            // const customerId = getCustomerId(); // Retrieve customer ID here
            const salesData = {
                customerId: customerId,
                movieTitle: movieTitle,
            };

            // Send the sales data to the server for each item in the cart
            $.ajax({
                dataType: "json",
                method: "POST",
                url: "api/sales", // Replace with the actual URL for your SalesServlet
                data: JSON.stringify(salesData),
                contentType: "application/json; charset=utf-8",
                success: function() {
                    // Handle success for each sale
                    console.log("Sales data submitted successfully for movie: " + movieTitle);
                },
                error: function(xhr, status, error) {
                    console.log("Sales error - Status: " + xhr.status);
                    console.log("Response Text: " + xhr.responseText);
                    console.log("Error: " + error);
                    console.error("Sales data submission failed for movie: " + movieTitle);
                }
            });
        }
    } catch (error) {
        console.error("Error parsing cart data:", error);
    }
}

let customerId = getParameterByName("id");
let cartData = getParameterByName("cartData");
// Parse the cart data if it's in JSON format
if (cartData) {
    try {
        let cartItems = JSON.parse(cartData);
        const total = cartItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
        const totalPriceElement = document.querySelector('.overall-price');
        if (totalPriceElement) totalPriceElement.textContent = `$${total.toFixed(2)}`;
        // Now you can work with the cartItems array on the payment page
    } catch (error) {
        console.error("Error parsing cart data:", error);
    }
}