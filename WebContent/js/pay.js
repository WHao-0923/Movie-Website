// Load total price from server
$.get('api/cart', function(data) {
    $('#price').text(data.total);
    if (data.total<=0){
        window.location.href ="cart.html";
    }
});

// Handle form submission
$('#payment-form').on('submit', function(event) {
    event.preventDefault();

    const firstName = $('#first-name').val();
    const lastName = $('#last-name').val();
    const cardNumber = $('#card-number').val();
    const expirationDate = $('#expiration-date').val();
    console.log("paying"+expirationDate)

    $.ajax({
        url: 'api/pay', // Update this URL to your server's endpoint
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            first_name:firstName,
            last_name:lastName,
            card_number:cardNumber,
            expiration:expirationDate
        }),
        success: function(response) {
            // Redirect to confirmation page if successful or cart.html if empty
            window.location.href = response["message"];
        },
        error: function() {
            alert("Invalid information.");
        }
    });
});
