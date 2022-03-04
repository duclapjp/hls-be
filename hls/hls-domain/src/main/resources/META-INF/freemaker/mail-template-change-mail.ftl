<#escape x as x?html>
<html>
    <body>
      <div>
        <p>あなたのメールアドレスを${newEmail}に変更したことを確認します。</p>
        <p>メールアドレスを変更せずに現在のメールアドレスを引き続き使用する場合は、このメールを無視してください。</p>
        <p>この変更を確認するまで、現在のメールアドレスを使用してHLSにログインする必要があります</p>
        <p>${confirmEmailUrl}</p>
        <p>上記のURLは今後24時間有効です</p>
      </div>
    </body>
</html>
</#escape>