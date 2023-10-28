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
            <th>Cart</th>
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
        if (item.genre1_name){
            genresCell.innerHTML = '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre1_name +'">'
                + item.genre1_name + '</a>';
            genresCell.innerHTML += ' '
        }
        if (item.genre2_name && (item.genre2_name != item.genre1_name)){
            genresCell.innerHTML += '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre2_name +'">'
                + item.genre2_name + '</a>';
            genresCell.innerHTML += ' '
        }
        if (item.genre3_name && (item.genre3_name != item.genre2_name)){
            genresCell.innerHTML += '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre3_name +'">'
                + item.genre3_name + '</a>';
        }

        resultRow.appendChild(genresCell);

        // Create and populate the stars cell
        const starsCell = document.createElement("td");
        starsCell.innerHTML += '<a href="single-star.html?star_id=' + item.star1_id +'">'
            + item.star1_name + '</a>';
        starsCell.innerHTML += ' '
        starsCell.innerHTML += '<a href="single-star.html?star_id=' + item.star2_id +'">'
            + item.star2_name + '</a>';
        starsCell.innerHTML += ' '
        starsCell.innerHTML += '<a href="single-star.html?star_id=' + item.star3_id +'">'
            + item.star3_name + '</a>';
        resultRow.appendChild(starsCell);

        // Create and populate the rating cell
        const ratingCell = document.createElement("td");
        ratingCell.innerText = item.rating || ""; // If rating is not available, show empty string
        resultRow.appendChild(ratingCell);

        const cartCell = document.createElement("td");
        // Create the button element
        const addToCartButton = document.createElement("button");
        addToCartButton.innerText = "Add to Cart";
        addToCartButton.className = "addToCartButton"; // Optional: if you want to apply styles via CSS
        // Add event listener to the button for functionality
        addToCartButton.addEventListener('click', function() {
            $.ajax({
                url: `api/add?movie_id=${item.id}`,
                type: 'GET',
                dataType: 'json',
                success: function(data) {
                    alert(data["message"])
                },
                error: function(error) {
                    console.error("Error fetching search results:", error);
                }
            });
        });

        cartCell.appendChild(addToCartButton);
        resultRow.appendChild(cartCell);


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
    titleCell.innerHTML = '<a href="index.html?title=' + "*" + '&year=&director=&star=&genre=">'
        + '*' + '</a>';
    resultRow.appendChild(titleCell);
    allResultsDiv.appendChild(resultRow);

    resultData.forEach(item => {
        const resultRow = document.createElement("tr");

        const titleCell = document.createElement("td");
        //console.log(item)
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

const preBtn = document.getElementById("previous");
const nextBtn = document.getElementById("next");

const sortBtn = document.getElementById("sortBtn")

let current_page = "1";
let pageSize = "10";
let sortValue = "title";
let sortOrder = "ASC";

document.getElementById('pageSize').addEventListener('change', function() {
    pageSize = this.value;
    current_page = 1;  // reset to the first page
    performSearch();
});
document.getElementById('sort').addEventListener('change', function() {
    sortValue = this.value  // reset to the first page
});
document.getElementById('sortOrder').addEventListener('change', function() {
    sortOrder = this.value  // reset to the first page
});

preBtn.addEventListener('click', function() {
    if(current_page > 1) {
        current_page--;
        performSearch();
    }
});

nextBtn.addEventListener('click', function() {
    current_page++;
    performSearch();
});

searchBtn.addEventListener('click', function() {
    performSearch();
});

sortBtn.addEventListener('click', function() {
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
    let page = current_page;
    let page_size = pageSize;
    let sortBy = sortValue.trim();
    let sortTitle = sortOrder.trim();
    if (!title && !year && !director && !star) {
        if (!window.location.href.endsWith('index.html')){
            genre = getParameterByName('genre')
            title = getParameterByName('title');
            year = getParameterByName('year');
            director = getParameterByName('director');
            star = getParameterByName('star');
        }
    }

    //console.log(title,encodeURIComponent(title))
    // if (title || year || director || star) {
    $.ajax({
        url: `api/main_page?title=${encodeURIComponent(title)}&year=${encodeURIComponent(year)}&director=${encodeURIComponent(director)}&star=${encodeURIComponent(star)}
                &genre=${encodeURIComponent(genre)}&page=${encodeURIComponent(page)}
                &pageSize=${encodeURIComponent(page_size)}&sortBy=${encodeURIComponent(sortBy)}
                &sortTitle=${encodeURIComponent(sortTitle)}`,
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
performSearch(current_page);
