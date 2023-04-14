# client-chat application

## contents
0. [About](https://github.com/PavelSav1n/client-chat#0-about)
1. [client-chat application details](https://github.com/PavelSav1n/client-chat#1-client-chat-application-details)
2. [Chat commands](https://github.com/PavelSav1n/client-chat#2-chat-commands)
3. [Other settings](https://github.com/PavelSav1n/client-chat#3-other-settings)



## 0. About

`Client-server chat application` project was developed as a final task of ITsJAVA Java Developer course. The aim was to develop a client-server chat application with following features:
- Client-server architecture
- Observer pattern implementation
- SQL database integration
- Multiple users
- Authorisation/registration process
- Password encryption
- Logging
- Saving data to file
- Implementation of some commands, like private messaging & message history
 
 Time elapsed: approximately 2 weeks.
 
 Full project `Client-server chat application` consists of two parts:
- [server-chat application](https://github.com/PavelSav1n/server-chat)
- [client-chat](https://github.com/PavelSav1n/client-chat)

## 1. client-chat application details

To run `client-chat` application you will need to start `server-chat` application first. If it is an initial start, you will need to create a user by choosing "2" menu item:

```Python
    **** CHAT ****
    1 -- Войти в чат
    2 -- Новый пользователь
    !help -- доступные команды
    !exit -- Выйти из программы
```
```
**** Регистрация нового пользователя ****
Введите логин нового пользователя: Test
Введите пароль нового пользователя: Test
**** Регистрация прошла успешно ****
```
After you've finished with registration, you can start chatting right away.

## 2. Chat commands

Here some commands, you can enter, once you've entered the chat:
```
!pm RECIPIENT_NAME MESSAGE -- send private message to recepient. Example: !pm Ivan Hello, Ivan!
!printLast X -- print in console last 'X' messages (all public & private messages to this user). Example: !printLast 10
!who -- print in console all chat participants
!save DESTINATION -- save chat all messages to file. Example: !save d:\message.txt
!exit -- exit chat to main menu
```

## 3. Other settings

`client-chat` application has different layers of logging, which you can change in [log4j.properties](https://github.com/PavelSav1n/client-chat/blob/master/src/main/resources/log4j.properties). Some basic information about how to set up Log4j is present in those properties via comments.

:arrow_up_small:[Back to contents](https://github.com/PavelSav1n/client-chat#contents)
