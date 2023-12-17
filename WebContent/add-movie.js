var addMovieBtn = $("#add_movie_button");
var movieForm = $("#movie_form");


function submitAddMovie(event) {
    console.log("submit add movie fucntion called");
    event.preventDefault();


    $.ajax({
        url: "api/addmovie",
        method: "POST",
        dataType: "json",  // Use lowercase "json"
        data: movieForm.serialize(),
        success: function(addMovieResponse) {
            // Handle the success response here
            console.log("SubmitMovie function is called");
            alert(addMovieResponse["message"]);
            // console.log(addStarResponse);
        },

        error: function(error) {
            // Handle the error response here
            console.error("Error in ajax call:", error);
        }
    });
}

// Bind the click event to the submitAddStar function
addMovieBtn.click(submitAddMovie);
