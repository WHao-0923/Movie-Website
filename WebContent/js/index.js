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
    console.log("handleMoviesResult: populating movies table from resultData");

    // Clear previous results
    // resultsTable.innerHTML = `
    // <tbody id="allResults">
    //
    // </tbody>`;
    tableHeads.innerHTML = `
    <thead>
        <tr>
            <th>Title</th>
            <th>Year</th>
            <th>Director</th>
            <th>Genres</th>
            <th>Stars</th>
            <th>Rating</th>
        </tr>
    </thead>
    `;
    allResultsDiv.innerHTML = "";
    // Iterate through resultData and display the results
    resultData.forEach(item => {
        const resultRow = document.createElement("tr"); // Create a new table row

        // Create and populate the title cell
        const titleCell = document.createElement("td");
        titleCell.innerHTML = '<a href="single-movie.html?movie_id=' + item.id + '"a>' +
            item.title;
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
        allResultsDiv.appendChild(resultRow);
    });

// If results were added, display the table
//     if (resultData.length > 0) {
//         document.getElementById("resultsTable").style.display = "block";
//     } else {
//         document.getElementById("resultsTable").style.display = "none";
//     }

}

function handleTitlesResult(resultData){
    console.log("handleTitlesResult: populating title table from resultData");

    // Clear previous results
    allResultsDiv.innerHTML = "";
    tableHeads.innerHTML = `
    <thead>
        <tr>
            <th>MOVIE TITLE INITIALS</th>
        </tr>
    </thead>
    `;
    // Iterate through resultData and display the results
    const resultRow = document.createElement("tr");
    const titleCell = document.createElement("td");
    titleCell.innerHTML = '<a href="index.html?title=&year=&director=&star=&genre=">'
        + '*' + '</a>';
    resultRow.appendChild(titleCell);
    allResultsDiv.appendChild(resultRow);

    resultData.forEach(item => {
        const resultRow = document.createElement("tr");

        const titleCell = document.createElement("td");
        console.log(item)
        titleCell.innerHTML = '<a href="index.html?title=' + item + '&year=&director=&star=&genre=">'
            + item.toUpperCase() + '</a>';
        resultRow.appendChild(titleCell);

        allResultsDiv.appendChild(resultRow);
    });

}

function handleGenresResult(resultData){
    console.log("handleGenresResult: populating genre table from resultData");

    // Clear previous results
    tableHeads.innerHTML = `
    <thead>
        <tr>
            <th>ALL MOVIE GENRES</th>
        </tr>
    </thead>
    `;
    allResultsDiv.innerHTML = "";

    // Iterate through resultData and display the results
    resultData.forEach(item => {
        const resultRow = document.createElement("tr");

        const genreCell = document.createElement("td");
        genreCell.innerHTML = '<a href="index.html?title=&year=&director=&star=&genre=' + item[1] +'">'
            + item[1] + '</a>';
        resultRow.appendChild(genreCell);

        allResultsDiv.appendChild(resultRow);
    });
}

console.log("handleStarResult: populating movies table from resultData");

const searchTitle = document.getElementById('searchTitle');
const searchYear = document.getElementById('searchYear');
const searchDirector = document.getElementById('searchDirector');
const searchStar = document.getElementById('searchStar');
const searchBtn = document.getElementById('submit');
const allResultsDiv = document.getElementById('allResults');
const resultsTable = document.getElementById('resultsTable')
const tableHeads = document.getElementById("tableHeads")

const titlesBtn = document.getElementById('browse_by_titles');
const genresBtn = document.getElementById('browse_by_genres');


searchBtn.addEventListener('click', function() {
    performSearch();
});

titlesBtn.addEventListener('click', function() {
    performTitles();
});
genresBtn.addEventListener('click', function() {
    performGenres();
});

function getParameterByName(name) {
    let match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
function performSearch() {
    let title = searchTitle.value.trim();
    let year = searchYear.value.trim();
    let director = searchDirector.value.trim();
    let star = searchStar.value.trim();
    let genre = '';
    if (!title && !year && !director && !star) {
        //console.log('INNNNNNNNNNNNN');
        genre = getParameterByName('genre')
        title = getParameterByName('title');
        year = getParameterByName('year');
        director = getParameterByName('director');
        star = getParameterByName('star');
    }

    //console.log(title,encodeURIComponent(title))
    // if (title || year || director || star) {
    $.ajax({
        url: `api/main_page?title=${encodeURIComponent(title)}&year=${encodeURIComponent(year)}&director=${encodeURIComponent(director)}&star=${encodeURIComponent(star)}
                &genre=${encodeURIComponent(genre)}`,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            if (data.redirect) {
                window.location.href = data.redirect; // Redirect with JavaScript
            } else {
                //console.log(data)
                handleMoviesResult(data);
            }
        },
        error: function(error) {
            console.error("Error fetching search results:", error);
        }
    });
    // }
    // else {
    //     alert("Please enter a search term in at least one of the fields.");
    // }
}

function performTitles(){
    $.ajax({
        url: `api/titles`,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            if (data.redirect) {
                window.location.href = data.redirect; // Redirect with JavaScript
            } else {
                //console.log(data)
                handleTitlesResult(data);
            }
        },
        error: function(error) {
            console.error("Error fetching search results:", error);
        }
    });
}

function performGenres(){
    $.ajax({
        url: `api/genres`,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            if (data.redirect) {
                window.location.href = data.redirect; // Redirect with JavaScript
            } else {
                //console.log(data)
                handleGenresResult(data);
            }
        },
        error: function(error) {
            console.error("Error fetching search results:", error);
        }
    });
}
performSearch();
