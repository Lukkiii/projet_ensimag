import { useContext, useEffect, useState } from 'react'
import { AppContext } from '../AppContext'


function Chat (){
    const {adminOuChat, tokenState, userState} = useContext(AppContext)
    const [allUsers, setAllUsers] = useState([])
    const [allMessages, setAllMessages] = useState([])

    useEffect(() => {
        fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/users', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            }
        }).then(response => response.json())
        .then(data => {
            setAllUsers(data.data)
        })
    }, [tokenState])

    useEffect(() => {
        const fetchMessages = () => {
            fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/messages/' + adminOuChat.groupId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'x-access-token': tokenState
                }
            }).then(response => response.json())
            .then(data => {
                if (data.status) {
                    setAllMessages(data.messages)
                }
                else {
                    alert(data.message)
                }
            })
        }

        fetchMessages()

        const intervalId = setInterval(fetchMessages, 5000)

        return () => clearInterval(intervalId)
        
    }, [adminOuChat.groupId, tokenState])

    async function sendMessage() {
        const message = document.getElementById('message').value
        if (message === '') {
            alert('Message should not be empty')
            return
        }
        const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/messages/' + adminOuChat.groupId, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            },
            body: JSON.stringify({
                content: message
            })
        })
        const data = await response.json()
        if (data.status) {
            document.getElementById('message').value = ''
            const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/messages/' + adminOuChat.groupId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'x-access-token': tokenState
                }
            })
            const data = await response.json()
            if (data.status) {
                setAllMessages(data.messages)
            }
            else {
                alert(data.message)
            }
        }
        else {
            alert(data.message)
        }
    }

    return (
        <div className='chatroom'>
            <fieldset>
                <legend>Discussion sur le groupe <strong>{ adminOuChat.groupName }</strong></legend>
                <fieldset>
                    <div className='groupMessages'>
                        {
                            allMessages && allMessages.map((message) => {
                                const isFromLoggedInUser = message.userId === userState.id
                                return (
                                    <div key={message.id} className={isFromLoggedInUser ? 'myMessage' : 'otherMessage'}>
                                        <div className='messageContent'>
                                            { message.content }
                                        </div>
                                        <div className='messageAuthor'>
                                            { allUsers && allUsers.find(user => user.id === message.userId).name }
                                        </div>
                                    </div>
                                )
                            })
                        }
                    </div>
                </fieldset>
                <fieldset>
                    <div className='inputMessage'>
                        <input type='text' id='message' placeholder='Entrez votre message' />
                        <button className="buttonEnvoyer" onClick={sendMessage}>Envoyer</button>
                    </div>
                </fieldset>
                
            </fieldset>
        </div>
    )


}

export default Chat