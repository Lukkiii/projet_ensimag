import { AppContext } from '../AppContext'
import { useContext } from 'react'
import MyGroups from './MyGroups'
import Extra from './Extra'

function Accueil() {
    const {userState, setTokenState, setUserState, setAdminOuChat} = useContext(AppContext)

    async function logout(){
        setTokenState(null)
        setUserState({
            id: '',
            name: '',
            email: ''
        })
        setAdminOuChat({
            adminOuChat: '',
            groupId: '',
            groupName: ''
        })

    }

    return (
        <div className="accueil">
            <p className="userLogout">{ userState.email } |<button className="buttonLogout" onClick={logout}>Se déconnecter</button></p>
            <div className="listGroup">
              <MyGroups/>
              <Extra/>  
            </div>  
        </div>
    )
}

export default Accueil