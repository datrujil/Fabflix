// Get the container element where we'll insert the alphabet letters
const alphabetContainer = document.getElementById('alphabet-container');
// $('#search_form').onsubmit;
function handleSearch() {

    // Get the values of the search form fields.
    const titleValue = $('[name="by-title"]').val();
    const yearValue = $('[name="by-year"]').val();
    const directorValue = $('[name="by-director"]').val();
    const starValue = $('[name="by-star"]').val();

    // Redirect the user to the search results page.
    window.location.assign(`movie-list-search.html?byTitle=${titleValue}&byYear=${yearValue}&byDirector=${directorValue}&byStar=${starValue}`);

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/single-genre",
        success: function(data) {
            // Open a new page with the search results.
            window.open(`movie-list-search.html?byTitle=${titleValue}&byYear=${yearValue}&byDirector=${directorValue}&byStar=${starValue}`);
        }
    });
}
// 0->9
for (let i = 0; i <= 9; i++) {
    let letter = i.toString();
    const hyperlink = document.createElement('a');
    hyperlink.href = `movie-list-search.html?startWith=${letter}`;
    hyperlink.textContent = letter;
    alphabetContainer.appendChild(hyperlink);
}

// Function to generate and add alphabet letters to the container
for (let i = 65; i <= 90; i++) {
    let letter = String.fromCharCode(i);
    const hyperlink = document.createElement('a');
    hyperlink.href = `movie-list-search.html?startWith=${letter}`;
    hyperlink.textContent = letter;
    alphabetContainer.appendChild(hyperlink);
}
// a *
const link = document.createElement("a");
link.href = "movie-list-search.html?startWith=*";
link.textContent = "*"; // Set the text content of the anchor to *

// Append the anchor element to the container
alphabetContainer.appendChild(link);


function handleGenresResult(resultData) {
    const h3 = document.createElement('h3');
    h3.textContent = 'Search by Genres';

    const genresHead = $('.genres_head');
    genresHead.append(h3);

    console.log("test ", resultData);
    let genres = document.getElementById("genres_links");
    for (let i = 0; i < resultData.length; i++) {
        const genresLink = document.createElement('a');
        // link for search by genres
        genresLink.href = `movie-list-search.html?genreId=${resultData[i]["genres-id"]}`;
        genresLink.textContent = resultData[i]["genres-name"];
        genres.appendChild(genresLink);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenresResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

