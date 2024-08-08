import { useContext, useState, useRef } from 'react'
import { AppContext } from '../AppContext'

function Register() {
    const nomRef = useRef(null)
    const emailRef = useRef(null)
    const passwordRef = useRef(null)
    const password2Ref = useRef(null)

    const [errorMessage, setErrorMessage] = useState("")
    const { userState, setUserState } = useContext(AppContext)

    async function validateInput(){
        if (!emailRef.current.value.match(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)) {
            setErrorMessage("Please enter a valid email.")
            return false
        }
        if (passwordRef.current.value.length < 6) {
            setErrorMessage("Password too short.")
            return false
        }
        if (password2Ref.current.value !== passwordRef.current.value) {
            setErrorMessage("Passwords do not match.")
            return false
        }
        setErrorMessage("")
        return true
    }

    async function register(){
        if (!await validateInput()) {
            return
        }
        
        const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/register/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: nomRef.current.value,
                email: emailRef.current.value,
                password: passwordRef.current.value
            })
        })

        const data = await response.json()

        if(!response.ok){
            setErrorMessage(data.message)
            return
        }
        
        nomRef.current.value = ""
        passwordRef.current.value = ""
        password2Ref.current.value = ""
        setUserState({
            ...userState,
            id: data.id,
            email: emailRef.current.value
        })
        emailRef.current.value = ""
        setErrorMessage("")
    }

    return (
        <fieldset className='register'>
            <legend>Pas encore de compte Enregistrez vous</legend>
            <div>
                <label>Nom
                    <input ref={nomRef} name="registerName" type="text" onChange={validateInput}/>
                </label>
            </div>
            <div>
                <label>Email
                    <input ref={emailRef} name="registerEmail" type="text" onChange={validateInput}/>
                </label>
            </div>
            <div>
                <label>Mot de Passe
                    <input ref={passwordRef} name="registerPassword" type="password" onChange={validateInput}/>
                </label>
            </div>
            <div>
                <label>Confirmer votre Mot de Passe
                    <input ref={password2Ref} name="registerPassword2" type="password" onChange={validateInput}/>
                </label>
            </div>
            <div>
                { {errorMessage} && <span style={{color:"red"}}> {errorMessage}</span> }
            </div>
            <div>
                <button className="buttonRegister" onClick={register}>OK</button>
            </div>
        </fieldset>
    )
}

export default Register