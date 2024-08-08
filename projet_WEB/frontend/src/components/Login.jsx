import { useContext, useState, useRef, useEffect } from 'react'
import { AppContext } from '../AppContext'

function Login() {
    
    const emailRef = useRef(null)
    const { userState } = useContext(AppContext)

    useEffect(() => {
        if (emailRef.current) {
            emailRef.current.value = userState.email;
        }
    }, [userState]);

    const passwordRef = useRef(null)
    const [errorMessage, setErrorMessage] = useState("")
    const { setTokenState, setUserState } = useContext(AppContext)

    async function validateInput(){
        if (!emailRef.current.value.match(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)) {
            setErrorMessage("Invalid email.")
            return false 
        }
        if (passwordRef.current.value.length < 6) {
            setErrorMessage("Password too short.")
            return false
        }
        setErrorMessage("")
        return true
    }

    async function login(){
        if (!await validateInput()) {
            return
        }
        const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/login/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
                body: JSON.stringify({
                email: emailRef.current.value,
                password: passwordRef.current.value
            })
        })
        const data = await response.json()
        if(data.token){
            setTokenState(data.token)
            setErrorMessage("")
            setUserState({
                id: data.user.id,
                name: data.user.name,
                email: data.user.email
            })
        } else {
            setErrorMessage(data.message)
        }
    }

    return (
        <fieldset className='login'>
            <legend>Se Connecter</legend>
            <div>
                <label>Email
                    <input ref={emailRef} name="loginEmail" type="text" onChange={validateInput}/>
                </label>
            </div>
            <div>
                <label>Mot de Passe
                    <input ref={passwordRef} name="loginPassword" type="password" onChange={validateInput}/>
                </label>
            </div>
            <div>
                <button className="buttonLogin" onClick={login}>OK</button>
            </div>
            <div>
                { {errorMessage} && <span style={{color:"red"}}> {errorMessage}</span> }
            </div>
        </fieldset>
    )
}

export default Login