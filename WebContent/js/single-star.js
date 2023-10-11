// 获取URL中的starId参数
function getStarIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('star_id');
}

const star_id = getStarIdFromUrl();

fetch(`/api/single-star?star_id=${star_id}`)
    .then(response => response.json())
    .then(data => {
        const tableBody = document.getElementById('data-table').querySelector('tbody');

        data.forEach(item => {
            const row = tableBody.insertRow();
            row.insertCell(0).textContent = item.star_id;
            row.insertCell(1).textContent = item.star_name;
            row.insertCell(2).textContent = item.star_dob;
            row.insertCell(3).textContent = item.movie_id;
            row.insertCell(4).textContent = item.movie_title;
            row.insertCell(5).textContent = item.movie_year;
            row.insertCell(6).textContent = item.movie_director;
        });
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });