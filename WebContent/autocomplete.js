function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");
    console.log("sending AJAX request to backend Java Servlet");

    //Check if the query is already cached in the frontend
    let cachedSuggestions = localStorage.getItem(query);
    if (cachedSuggestions) {
        console.log("Using cached suggestions for query:", query);
        let jsonData = JSON.parse(cachedSuggestions);
        doneCallback({ suggestions: jsonData });
    } else {
        // Send AJAX request to backend for new suggestions
        jQuery.ajax({
            method: "GET",
            url: "api/search-suggestion?query=" + encodeURIComponent(query),
            dataType: "text",
            success: function (data) {
                handleLookupAjaxSuccess(data, query, doneCallback);
                // Cache the fetched suggestions in LocalStorage
                localStorage.setItem(query, data);
            },
            error: function (errorData) {
                console.log("lookup ajax error");
                console.log(errorData);
            }
        });
    }
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")
    var jsonData = JSON.parse(data);

    // TODO: if you want to cache the result into a global variable you can do it here
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    if (suggestion) {
        manuallySelected = true;
        let movieId = suggestion["data"]["id"];
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/single-movie?id=" + movieId,
            success: (resultData) => handleStarResult(resultData)
        });
        window.location.href = `single-movie.html?id=${movieId}`;
    }
}

/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    autoSelectFirst: false,
});

/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/single-genre?startWith=" + query,
        success: (resultData) => {
            window.location.href = `movie-list-search.html?startWith=${query}`;
        }
    });
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode === 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// Reset manuallySelected flag when the autocomplete dropdown is closed
$('#autocomplete').on('autocomplete:dropdownHide', function() {
    let manuallySelected = false;
});