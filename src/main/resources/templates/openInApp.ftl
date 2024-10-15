<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="refresh" content="5;url=${redirectUrl}">
    <title>Redirecting...</title>
</head>
<body>
    <script>
        window.location.href = "${redirectUrl}";

        setTimeout(function () {
            window.location.href = "${storeUrl}";
        }, 3000); // Задержка для попытки открыть приложение
    </script>
</body>
</html>