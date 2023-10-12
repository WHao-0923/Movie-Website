function getParameterByName(name) {
    let match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

$(document).ready(function() {
    let movieId = getParameterByName('movie_id');

    $.ajax({
        type: "GET",
        url: `api/single-movie?movie_id=${movieId}`,
        success: function(data) {
            $('#main_title').text(data["movie_title"]);
            $('#movie_year').text(data["movie_year"]);
            $('#movie_director').text(data["movie_director"]);

            // Handle genres
            for (let i = 0; i < Math.min(10, data["genres"].length); i++)  {
                $('#movie_genres').append(`<span class="genre-tag">${data["genres"][i]["genre"]}</span> `);
            };

            // Handle stars
            for (let i = 0; i < Math.min(100, data["stars"].length); i++) {
                $('#movie_stars').append(`<li><a href="single-star.html?star_id=${data["stars"][i]["star_id"]}" class="star-tag">${data["stars"][i]["star_name"]}</a> `);
            };

            $('#movie_rating').text(data["movie_rating"]);
        },
        error: function(error) {
            console.log("Error:", error);
        }
    });
});
