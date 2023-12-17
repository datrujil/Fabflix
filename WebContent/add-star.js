var addStarBtn = $("#add_star_button");
var starForm = $("#star_form");


function submitAddStar(event) {
    console.log("submit addstar");
    event.preventDefault();

    $.ajax({
        url: "api/addstar",
        method: "POST",
        dataType: "json",  // Use lowercase "json"
        data: starForm.serialize(),
        success: function(addStarResponse) {
            // Handle the success response here
            console.log("SubmitAddStart function is called");
            alert(addStarResponse["message"]);
            // console.log(addStarResponse);
        },
        error: function(error) {
            // Handle the error response here
            console.error("Error in ajax call:", error);
        }
    });
}

// Bind the click event to the submitAddStar function
addStarBtn.click(submitAddStar);
