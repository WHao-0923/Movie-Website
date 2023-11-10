document.addEventListener('DOMContentLoaded', function() {
    fetchMetadata();
});

document.getElementById('addStarForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const starName = document.getElementById('starName').value;
    const birthYear = document.getElementById('birthYear').value;
    addStar(starName, birthYear);
});

document.getElementById('addMovieForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const movieTitle = document.getElementById('movieTitle').value;
    const starNameMovie = document.getElementById('starNameMovie').value;
    const genreName = document.getElementById('genreName').value;
    addMovie(movieTitle, starNameMovie, genreName);
});

function fetchMetadata() {
    fetch('api/metadata')
        .then(response => response.json())
        .then(data => displayMetadata(data))
        .catch(error => console.error('Error fetching metadata:', error));
}

function displayMetadata(data) {
    const display = document.getElementById('metadataDisplay');
    display.innerHTML = '<h3>Database Tables and Attributes:</h3>';

    let tables = {};

    // Organize data by table
    data.cols.forEach(col => {
        if (!tables[col.table_name]) {
            tables[col.table_name] = [];
        }
        tables[col.table_name].push({ name: col.col, type: col.type });
    });

    // Display each table and its columns
    for (let tableName in tables) {
        let content = `<h4>${tableName}</h4><ul>`;
        tables[tableName].forEach(attr => {
            content += `<li>${attr.name} (${attr.type})</li>`;
        });
        content += '</ul>';
        display.innerHTML += content;
    }
}

function addStar(name, year) {
    // Replace with actual logic to send data to 'api/addStar'
    console.log('Adding Star:', name, year);
    // Implement the POST request here
}

function addMovie(title, starName, genre) {
    // Replace with actual logic to send data to 'api/addMovie'
    console.log('Adding Movie:', title, starName, genre);
    // Implement the POST request here
}