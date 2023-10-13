/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleStarResult: populating movies table from resultData");

    // Populate the star table
    // Find the empty table body by id "movies_table_body"
    let moviesTableBodyElement = jQuery("#movies_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +
            '<a href="single-movie.html?movie_id=' + resultData[i]['movie_id'] + '">' +
            resultData[i]["movie_title"] +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" +
            resultData[i]["movie_genre1"];
        if (resultData[i]["movie_genre2"] != resultData[i]["movie_genre1"]){
            rowHTML += ", " + resultData[i]["movie_genre2"] ;
            if (resultData[i]["movie_genre3"] != resultData[i]["movie_genre2"]){
                rowHTML += ", " + resultData[i]["movie_genre3"] ;
            }
        }
        rowHTML += "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to index.html with id passed with GET url parameter
            '<a href="single-star.html?star_id=' + resultData[i]['movie_star1_id'] + '">'
            + resultData[i]["movie_star1_name"] +     // display movie_title for the link text
            '</a>' + ", " +
            '<a href="single-star.html?star_id=' + resultData[i]['movie_star2_id'] + '">'
            + resultData[i]["movie_star2_name"] +     // display movie_title for the link text
            '</a>' + ", " +
            '<a href="single-star.html?star_id=' + resultData[i]['movie_star3_id'] + '">'
            + resultData[i]["movie_star3_name"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        moviesTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});