/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

function handleMoviesResult(resultData) {
    console.log("handleStarResult: populating movies table from resultData");

    // Clear previous results
    searchResultsDiv.innerHTML = "";
    // Iterate through resultData and display the results
    resultData.forEach(item => {
        const resultRow = document.createElement("tr"); // Create a new table row

        // Create and populate the title cell
        const titleCell = document.createElement("td");
        titleCell.innerText = item.title || ""; // If title is not available, show empty string
        resultRow.appendChild(titleCell);

        // Create and populate the year cell
        const yearCell = document.createElement("td");
        yearCell.innerText = item.year || ""; // If year is not available, show empty string
        resultRow.appendChild(yearCell);

        // Create and populate the director cell
        const directorCell = document.createElement("td");
        directorCell.innerText = item.director || ""; // If director is not available, show empty string
        resultRow.appendChild(directorCell);

        // Create and populate the genres cell
        const genresCell = document.createElement("td");
        genresCell.innerText = (item.genre1_name + " ") || "";
        genresCell.innerText += (item.genre2_name + " ") || "";
        genresCell.innerText += item.genre3_name || ""; // If genre is not available, show empty string
        resultRow.appendChild(genresCell);

        // Create and populate the stars cell
        const starsCell = document.createElement("td");
        starsCell.innerText = (item.star1_name + ", ") || "";
        starsCell.innerText += (item.star2_name + ", ") || "";
        starsCell.innerText += item.star3_name || ""; // If genre is not available, show empty string
        resultRow.appendChild(starsCell);

        // Create and populate the rating cell
        const ratingCell = document.createElement("td");
        ratingCell.innerText = item.rating || ""; // If rating is not available, show empty string
        resultRow.appendChild(ratingCell);

        // Append the filled row to the results table
        searchResultsDiv.appendChild(resultRow);
    //console.log(searchResultsDiv.innerHTML)
    });

// If results were added, display the table
    if (resultData.length > 0) {
        document.getElementById("resultsTable").style.display = "block";
    } else {
        document.getElementById("resultsTable").style.display = "none";
    }

}

console.log("handleStarResult: populating movies table from resultData");

const searchTitle = document.getElementById('searchTitle');
const searchYear = document.getElementById('searchYear');
const searchDirector = document.getElementById('searchDirector');
// const searchStar = document.getElementById('searchStar');
const searchBtn = document.getElementById('submit');
const searchResultsDiv = document.getElementById('searchResults');

$.ajax({
    url: `api/main_page?title=&year=&director=login=`,
    type: 'GET',
    dataType: 'json',
    success: function(data) {
        handleMoviesResult(data);
    },
    error: function(error) {
        console.error("Error fetching search results:", error);
    }
});

searchBtn.addEventListener('click', function() {
    performSearch();
});

function performSearch() {
    const title = searchTitle.value.trim();
    const year = searchYear.value.trim();
    const director = searchDirector.value.trim();
    // const star = searchStar.value.trim();
    console.log(title,encodeURIComponent(title))
    if (title || year || director) {
        $.ajax({
            url: `api/main_page?title=${encodeURIComponent(title)}&year=${encodeURIComponent(year)}&director=${encodeURIComponent(director)}`,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                handleMoviesResult(data);
            },
            error: function(error) {
                console.error("Error fetching search results:", error);
            }
        });
    } else {
        alert("Please enter a search term in at least one of the fields.");
    }
}


