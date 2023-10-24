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
            for (let i = 0; i < Math.min(3, data["genres"].length); i++)  {
                $('#movie_genres').append(`<li><a href="index.html?genre_id=${data["genres"][i]["genre_id"]}" class="genre-tag">${data["genres"][i]["genre"]}</a> `);
            };

            // Handle stars
            for (let i = 0; i < Math.min(100, data["stars"].length); i++) {
                $('#movie_stars').append(`<li><a href="single-star.html?star_id=${data["stars"][i]["star_id"]}" class="star-tag">${data["stars"][i]["star_name"]}</a> `);
            };

            $('#movie_rating').text(data["movie_rating"]);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 401) {  // Check if status code is 401 Unauthorized
                window.location.href = 'login.html';  // Redirect to login.html
            }
        }
    });
});
