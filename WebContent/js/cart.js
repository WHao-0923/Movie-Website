document.addEventListener('DOMContentLoaded', function () {
    updateCart();
});

var totalPrice = 0;

function updateCart() {
    fetch('api/cart')
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('#cart-table tbody');
            tbody.innerHTML = '';
            // let totalPrice = 0;
            data.previousItems.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.title}</td>
                    <td>
                        <div class="quantity-actions">
                            <button onclick="updateQuantity('${item.movie_id}', 1,'decrease')">-</button>
                            <input type="number" class="quantity-input" value="${item.quantity}" onchange="setQuantity('${item.movie_id}', this.value)">
                            <button onclick="updateQuantity('${item.movie_id}', 1,'add')">+</button>
                        </div>
                    </td>
                    <td>$${item.price.toFixed(2)}</td>
                    <td>$${(item.price * item.quantity).toFixed(2)}</td>
                    <td><button onclick="removeFromCart('${item.movie_id}')">Remove</button></td>
                `;
                tbody.appendChild(row);
                totalPrice += item.price * item.quantity;
            });
            document.getElementById('total-price').textContent = `Total Price: $${totalPrice.toFixed(2)}`;
        })
        .catch(error => console.error('Error updating cart:', error));
}

function updateQuantity(mid, change,actionType) {

    fetch('api/cart', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({movie_id:mid,type:actionType,num:change})
    })
        .then(response => response.json())
        .then(data => {
            if (data.actionDone) {
                updateCart();
            } else {
                alert('Error updating quantity');
            }
        })
        // .catch(error => console.error('Error updating quantity:', error));
}

function setQuantity(mid, quantity,ty) {
    const change = quantity - document.querySelector(`input[value='${mid}']`).value;
    updateQuantity(mid, change,ty);
}

function removeFromCart(mid) {
    updateQuantity(mid, 0,"delete");
}

function proceedToPayment() {
    if (totalPrice<=0){
        alert("Empty cart!");
    }
    else{
        window.location.href = 'pay.html';
    }
}
