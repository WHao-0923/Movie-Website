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
    const director = document.getElementById('director').value;
    const year = document.getElementById("year").value;
    addMovie(movieTitle, starNameMovie, genreName,director,year);
});

function fetchMetadata() {
    try {
        fetch('api/metadata')
            .then(response => response.json())
            .then(data => displayMetadata(data))
    }catch (error) {
        window.location.href = 'eLogin.html';
    }
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

function isVaildYear(year){
    if (year){
        const now = new Date();
        const now_year = now.getFullYear();
        if (year>now_year || year<1800){
            return false;
        }
    }
    return true;
}


async function addStar(name, year) {
    // Replace with actual logic to send data to 'api/addStar'
    if (!isVaildYear(year)){
        alert("invalid birth year!");
        return;
    }
    console.log('Adding Star:', name, year);
    // Implement the POST request here
    try {
        const response = await fetch('api/addStar', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name: name, birth: year})
        });

        const data = await response.json();

        if (response.ok) {
            // 如果登录成功，重定向到主页面
            alert("Adding Star Succeed! StarId:"+data["starId"]);
        } else {
            // 如果登录失败，显示错误消息
            alert(data["message"]);
        }
    } catch (error) {
        alert("internal error happens");
    }
}

async function addMovie(title, starName, genre, director, year) {
    if (!isVaildYear(year)) {
        alert("invalid year!");
        return;
    }
    console.log('Adding Movie:', title, starName, genre, director, year);
    try {
        const response = await fetch('api/addMovie', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({title: title, starName: starName, genre: genre,director:director,year:year})
        });

        if (response.ok) {
            // 如果登录成功，重定向到主页面
            const data = await response.json();
            alert(data['message']);
        } else {
            window.location.href = 'eLogin.html';
        }
    } catch (error) {
        console.error('An unexpected error occurred:', error);
        document.getElementById('error').innerText = 'An unexpected error occurred. Please try again later.';
    }
}