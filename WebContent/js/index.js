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
            genresCell.innerHTML = '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre1_name +'&refresh=true">'
                + item.genre1_name + '</a>';
            genresCell.innerHTML += ' '
        }
        if (item.genre2_name && (item.genre2_name != item.genre1_name)){
            genresCell.innerHTML += '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre2_name +'&refresh=true">'
                + item.genre2_name + '</a>';
            genresCell.innerHTML += ' '
        }
        if (item.genre3_name && (item.genre3_name != item.genre2_name)){
            genresCell.innerHTML += '<a href="index.html?title=&year=&director=&star=&genre=' + item.genre3_name +'&refresh=true">'
                + item.genre3_name + '</a>';
        }

        resultRow.appendChild(genresCell);

        // Create and populate the stars cell
        const starsCell = document.createElement("td");
        starsCell.innerHTML += '<a href="single-star.html?star_id=' + item.star1_id +'">'
            + item.star1_name + '</a>';
        starsCell.innerHTML += ', '
        starsCell.innerHTML += '<a href="single-star.html?star_id=' + item.star2_id +'">'
            + item.star2_name + '</a>';
        starsCell.innerHTML += ', '
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
    titleCell.innerHTML = '<a href="index.html?title=' + "*" + '&year=&director=&star=&genre=&refresh=true">'
        + '*' + '</a>';
    resultRow.appendChild(titleCell);
    allResultsDiv.appendChild(resultRow);

    resultData.forEach(item => {
        const resultRow = document.createElement("tr");

        const titleCell = document.createElement("td");
        //console.log(item)
        titleCell.innerHTML = '<a href="index.html?title=' + item + '&year=&director=&star=&genre=&refresh=true">'
            + item.toUpperCase() + '</a>';
        resultRow.appendChild(titleCell);

        allResultsDiv.appendChild(resultRow);
    });

}

function handleGenresResult(resultData){
    //console.log("handleGenresResult: populating genre table from resultData");

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
        genreCell.innerHTML = '<a href="index.html?title=&year=&director=&star=&genre=' + item[1] +'&refresh=true">'
            + item[1] + '</a>';
        resultRow.appendChild(genreCell);

        allResultsDiv.appendChild(resultRow);
    });
}

let activeSuggestionIndex = -1;

//console.log("handleStarResult: populating movies table from resultData");

let searchTitle = document.getElementById('searchTitle');
let searchYear = document.getElementById('searchYear');
let searchDirector = document.getElementById('searchDirector');
let searchStar = document.getElementById('searchStar');
const searchBtn = document.getElementById('submit');
const allResultsDiv = document.getElementById('allResults');
const resultsTable = document.getElementById('resultsTable')
const tableHeads = document.getElementById("tableHeads")

const titlesBtn = document.getElementById('browse_by_titles');
const genresBtn = document.getElementById('browse_by_genres');

const preBtn = document.getElementById("previous");
const nextBtn = document.getElementById("next");

const sortBtn = document.getElementById("sortBtn")

//let fulltext = document.getElementById("fullTextSearch");
const fullTextBtn = document.getElementById("fullTextSubmit");
//const suggestionListElement = document.getElementById('suggestionList'); // Your suggestion list container

let saved_suggestions = null;
let fulltext = document.getElementById('fullTextSearch');
document.addEventListener("DOMContentLoaded", function() {
    let fulltext = document.getElementById('fullTextSearch');
    let suggestionListElement = document.getElementById('suggestionList');
    fullTextBtn.addEventListener('click', function () {
        sessionStorage.setItem('fullText', fulltext.value);
        performSearch(false, true);
    });


    fulltext.addEventListener('input', debounce(function (e) {
        //console.log(e.key)
        const query = e.target.value;
        if (query.length >= 3) {
            console.log("Auto-complete Initiated - 300ms")
            performAutoComplete(query); // This function needs to be defined to handle the search
        }
    }, 300));

    function debounce(func, delay) {
        let debounceTimer;
        return function () {
            const context = this;
            const args = arguments;
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => func.apply(context, args), delay);
        };
    }
    fulltext.addEventListener('keydown', function(e) {
        //console.log(e.key);
        if (["ArrowDown", "ArrowUp", "Enter"].includes(e.key)) {
            handleKeyboardNavigation(e); // This function needs to be defined to handle keyboard events
        }
    });
    document.addEventListener('click', function(e) {
        if (e.target.id !== 'fullTextSearch') {
            suggestionListElement.style.display = 'none';
        }
    });

    function handleKeyboardNavigation(e) {
        const suggestions = document.querySelectorAll('.suggestion');
        let isNavigationKey = false;
        //console.log("In Navigation");
        if (suggestions.length > 0) {
            if (e.key === "ArrowDown") {
                //console.log("ArrowDown Detected");
                isNavigationKey = true;
                if (activeSuggestionIndex < suggestions.length - 1) {
                    activeSuggestionIndex++;
                }
            } else if (e.key === "ArrowUp") {
                isNavigationKey = true;
                if (activeSuggestionIndex > 0) {
                    activeSuggestionIndex--;
                }
            } else if (e.key === "Enter") {
                if (activeSuggestionIndex >= 0 && activeSuggestionIndex < suggestions.length) {
                    e.preventDefault(); // Prevent form submission or other default behavior
                    selectSuggestion(saved_suggestions[activeSuggestionIndex]);
                    return;
                } else {
                    sessionStorage.setItem('fullText', fulltext.value);
                    suggestionListElement.style.display = 'none';
                    performSearch(false, true);
                }
            }
        } else if (e.key === "Enter"){
            sessionStorage.setItem('fullText', fulltext.value);
            suggestionListElement.style.display = 'none';
            performSearch(false, true);
        }

        if (isNavigationKey) {
            //console.log(activeSuggestionIndex);
            e.preventDefault(); // Prevent default arrow key behavior in the textbox
            updateSuggestionListUI(suggestions);
        }
    }

    function selectSuggestion(suggestion) {
        window.location.href = "single-movie.html?movie_id=" + suggestion.id;
    }
    function performAutoComplete(query) {
        // Check if the query result is cached
        const cachedResults = sessionStorage.getItem(query);
        if (cachedResults) {
            console.log("Using cached suggestions")
            const suggestions = JSON.parse(cachedResults);
            console.log(suggestions)
            updateSuggestionList(suggestions);
        } else {
            console.log("Using new suggestions")
            $.ajax({
                url: `api/autocomplete?query=${encodeURIComponent(query)}`, // Update with the correct API endpoint and parameters
                type: 'GET',
                dataType: 'json',
                success: function(data) {
                    // Assuming 'data' contains the list of suggestions
                    const suggestions = data.slice(0, 10); // Limit to 10 entries
                    sessionStorage.setItem(query, JSON.stringify(suggestions));
                    saved_suggestions = suggestions;
                    console.log(suggestions)
                    updateSuggestionList(suggestions);
                },
                error: function(error) {
                    console.error("Error fetching autocomplete suggestions:", error);
                }
            });
        }
    }

    function updateSuggestionList(suggestions) {
        activeSuggestionIndex = -1;
        // Clear previous results
        suggestionListElement.innerHTML = '';

        // Display suggestions
        suggestions.forEach(suggestion => {
            let div = document.createElement('div');
            div.textContent = suggestion.title;
            //div.innerHTML = '<mark>' + div.textContent + '</mark>';
            div.classList.add('suggestion');
            div.addEventListener('click', function() {
                fulltext.value = this.textContent; // Update the input field
                suggestionListElement.style.display = 'none'; // Hide the suggestions
                // Optionally, perform an action, like redirecting to a search page
                selectSuggestion(suggestion);
            });
            suggestionListElement.appendChild(div);
        });

        suggestionListElement.style.display = 'block'; // Show suggestions
    }
    function updateSuggestionListUI() {
        const suggestions = document.querySelectorAll('.suggestion');
        suggestions.forEach((s, index) => {
            if (index === activeSuggestionIndex) {
                fulltext.value = s.textContent; // Update input field
                s.innerHTML = '<mark>' + s.textContent + '</mark>';
            } else {
                s.innerHTML = s.textContent;
            }
        });
    }

});


let current_page = 1;
if (sessionStorage.getItem('page')){
    current_page = sessionStorage.getItem("page");
}


let pageSize = "10";
let sortValue = "title";
let sortOrder = "ascasc";

document.getElementById('pageSize').addEventListener('change', function() {
    pageSize = this.value;
    current_page = 1;  // reset to the first page
    sessionStorage.setItem('pageSize', this.value);
    performSearch(false,false);
});
document.getElementById('sort').addEventListener('change', function() {
    sortValue = this.value  // reset to the first page
    sessionStorage.setItem('sort', this.value);
});
document.getElementById('sortOrder').addEventListener('change', function() {
    sortOrder = this.value  // reset to the first page
    sessionStorage.setItem('sortOrder', this.value);
});

preBtn.addEventListener('click', function() {
    if(current_page > 1) {
        current_page--;
        sessionStorage.setItem('page', current_page);
        performSearch(false,false);
    }
});

nextBtn.addEventListener('click', function() {
    current_page++;
    sessionStorage.setItem('page', current_page);
    performSearch(false,false);
});

searchBtn.addEventListener('click', function() {
    sessionStorage.setItem('searchTitle', searchTitle.value);
    sessionStorage.setItem('searchYear', searchYear.value);
    sessionStorage.setItem('searchDirector', searchDirector.value);
    sessionStorage.setItem('searchStar', searchStar.value);
    performSearch(true,false);
});

sortBtn.addEventListener('click', function() {
    performSearch(false,false);
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
function performSearch(search,fullText) {
    let title = searchTitle.value.trim();
    let year = searchYear.value.trim();
    let director = searchDirector.value.trim();
    let star = searchStar.value.trim();
    let genre = '';
    //sessionStorage.setItem("genre",null);
    let page = current_page;
    let page_size = pageSize;
    let sortBy = sortValue.trim();
    let sortTitle = sortOrder.trim();
    if (!title && !year && !director && !star && !fullText) {
        if (getParameterByName('genre')!=null){
            if (!window.location.href.endsWith('index.html')&&!window.location.href.endsWith('login.html')) {
                genre = getParameterByName('genre');
                sessionStorage.setItem("genre",genre);
                title = getParameterByName('title');
                year = getParameterByName('year');
                director = getParameterByName('director');
                star = getParameterByName('star');
            }
        }
    }

    if(!fullText && getParameterByName("refresh")==null || getParameterByName("refresh") != 'true'){
        console.log('########## restore');
        console.log(sessionStorage.getItem("genre"));
        if (search){
            sessionStorage.setItem("genre",'');
            genre = '';
        }
        if (!search && sessionStorage.getItem('genre')!='' &&
            (sessionStorage.getItem('searchTitle')!='' ||
                sessionStorage.getItem('searchYear')!='' ||
                sessionStorage.getItem('searchDirector')!='' ||
                sessionStorage.getItem('searchStar')!='')) {
            genre = sessionStorage.getItem('genre');
            console.log('########## CLEARED')
            sessionStorage.setItem('searchTitle', '');
            sessionStorage.setItem('searchYear', '');
            sessionStorage.setItem('searchDirector', '');
            sessionStorage.setItem('searchStar', '');
        }
        if (sessionStorage.getItem('page')) {
            page = sessionStorage.getItem('page').trim();
        }
        if (sessionStorage.getItem('pageSize')) {
            document.getElementById('pageSize').value = sessionStorage.getItem('pageSize');
            page_size = sessionStorage.getItem('pageSize').trim();
        }

        if (sessionStorage.getItem('sort')) {
            document.getElementById('sort').value = sessionStorage.getItem('sort');
            sortBy = sessionStorage.getItem('sort').trim();
        }

        if (sessionStorage.getItem('sortOrder')) {
            document.getElementById('sortOrder').value = sessionStorage.getItem('sortOrder');
            sortTitle = sessionStorage.getItem('sortOrder').trim();
        }

        if (sessionStorage.getItem('searchTitle')) {
            document.getElementById('searchTitle').value = sessionStorage.getItem('searchTitle');
            title = sessionStorage.getItem('searchTitle').trim();
        }

        if (sessionStorage.getItem('searchYear')) {
            document.getElementById('searchYear').value = sessionStorage.getItem('searchYear');
            year = sessionStorage.getItem('searchYear').trim();
        }

        if (sessionStorage.getItem('searchDirector')) {
            document.getElementById('searchDirector').value = sessionStorage.getItem('searchDirector');
            director = sessionStorage.getItem('searchDirector').trim();
        }

        if (sessionStorage.getItem('searchStar')) {
            document.getElementById('searchStar').value = sessionStorage.getItem('searchStar');
            star = sessionStorage.getItem('searchStar').trim();
        }
    }


    //console.log(title,encodeURIComponent(title))
    if (!fullText) {
        $.ajax({
            url: `api/main_page?title=${encodeURIComponent(title)}&year=${encodeURIComponent(year)}&director=${encodeURIComponent(director)}&star=${encodeURIComponent(star)}
                &genre=${encodeURIComponent(genre)}&page=${encodeURIComponent(page)}
                &pageSize=${encodeURIComponent(page_size)}&sortBy=${encodeURIComponent(sortBy)}
                &sortTitle=${encodeURIComponent(sortTitle)}&fullText=`,
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                if (data.redirect) {
                    window.location.href = data.redirect; // Redirect with JavaScript
                } else {
                    //console.log(data)
                    if (data.length >= 1) {
                        handleMoviesResult(data);
                    } else {
                        if (current_page > 1) {
                            current_page--;
                        }
                    }
                }
            },
            error: function (error) {
                console.error("Error fetching search results:", error);
            }
        });
    } else{
        let fullTextInput = fulltext.value.trim();
        console.log(fullTextInput);
        $.ajax({
            url: `api/main_page?title=${encodeURIComponent(title)}&year=${encodeURIComponent(year)}&director=${encodeURIComponent(director)}&star=${encodeURIComponent(star)}
                &genre=${encodeURIComponent(genre)}&page=${encodeURIComponent(page)}
                &pageSize=${encodeURIComponent(page_size)}&sortBy=${encodeURIComponent(sortBy)}
                &sortTitle=${encodeURIComponent(sortTitle)}&fullText=${encodeURIComponent(fullTextInput)}`,
            type: 'GET',
            dataType: 'json',
            success: function (data) {
                if (data.redirect) {
                    window.location.href = data.redirect; // Redirect with JavaScript
                } else {
                    //console.log(data)
                    if (data.length >= 1) {
                        handleMoviesResult(data);
                    } else {
                        if (current_page > 1) {
                            current_page--;
                        }
                    }
                }
            },
            error: function (error) {
                console.error("Error fetching search results:", error);
            }
        });
    }
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

performSearch(false,false);
