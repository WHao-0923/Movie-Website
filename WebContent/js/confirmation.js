document.addEventListener('DOMContentLoaded', () => {
    fetch('api/confirmation')
        .then(response => response.json())
        .then(data => {
            const confirmationDiv = document.getElementById('confirmation');
            data.previousItems.forEach(item => {
                const movieItem = document.createElement('div');
                movieItem.classList.add('movie-item');
                movieItem.innerHTML = `
          <div class="movie-title">${item.title}</div>
          <div class="movie-details">Price: $${item.price}</div>
          <div class="movie-details">Quantity: ${item.quantity}</div>
        `;
                confirmationDiv.appendChild(movieItem);
            });
            const transactionId = document.createElement('div');
            transactionId.classList.add('transaction-id');
            transactionId.innerHTML = `Transaction ID: ${data.sid}`;
            confirmationDiv.appendChild(transactionId);
        })
        .catch(error => console.error('Error fetching confirmation data:', error));
});