<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Подтверждение почты</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f8ff; /* Приятный светло-голубой фон */
            color: #333;
            margin: 0;
            padding: 20px;
        }

        .container {
            text-align: left;
        }

        .message {
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            color: #333; /* Цвет текста */
            margin-bottom: 20px;
        }

        .ellipsis {
            display: inline-block;
            font-size: 18px;
            color: #333;
        }

        @keyframes blink {
            0% { opacity: 0; }
            50% { opacity: 1; }
            100% { opacity: 0; }
        }

        .dot1, .dot2, .dot3 {
            animation: blink 1.5s infinite;
        }

        .dot2 {
            animation-delay: 0.3s;
        }

        .dot3 {
            animation-delay: 0.6s;
        }

        .join-go {
            color: #24befe; /* Цвет кнопки JOIN GO */
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            margin-top: 20px;
        }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const token = "${token}";  // Используем переменную из модели FreeMarker
            const messageElement = document.getElementById('message');

            // Показываем сообщение об ожидании
            messageElement.innerHTML = `Пожалуйста, подождите <span class="ellipsis">
                                            <span class="dot1">.</span><span class="dot2">.</span><span class="dot3">.</span>
                                        </span> Создаём для вас безопасный пароль`;


            // Делаем асинхронный запрос на сервер для подтверждения почты
            fetch(`/confirm-password-reset/process?token=${token}`)
                .then(response => response.text())
                .then(result => {
                    // Отображаем результат
                    messageElement.textContent = result;
                })
                .catch(error => {
                    messageElement.textContent = "Произошла ошибка. Попробуйте еще раз.";
                    console.error('Ошибка:', error);
                });
        });
    </script>
</head>
<body>
<div class="container">
    <div id="message" class="message">
        Пожалуйста, подождите <span class="ellipsis">
                <span class="dot1">.</span><span class="dot2">.</span><span class="dot3">.</span>
            </span> Создаём для вас безопасный пароль

    </div>
    <a class="join-go">JOIN GO</a> <!-- Кнопка JOIN GO -->
</div>
</body>
</html>
