document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const gRecaptchaResponse = document.getElementById('g-recaptcha-response').value;

    if (gRecaptchaResponse == 0){
        alert("please complete Recaptcha!");
        return
    }

    try {
        const response = await fetch('api/ELogin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email:email,password:password,gRecaptchaResponse:gRecaptchaResponse})
        });

        if (response.ok) {
            // 如果登录成功，重定向到主页面
            window.location.href = 'dashboard.html';
        } else {
            // 如果登录失败，显示错误消息
            const data = await response.json();
            document.getElementById('error').innerText = data['errorMessage'];
        }
    } catch (error) {
        console.error('An unexpected error occurred:', error);
        document.getElementById('error').innerText = 'An unexpected error occurred. Please try again later.';
    }
});
