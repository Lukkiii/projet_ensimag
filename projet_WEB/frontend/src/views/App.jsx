import { useState } from 'react'
import Login from '../components/Login'
import Accueil from '../components/Accueil'
import Register from '../components/Register'
import './App.css'
import { AppContext } from '../AppContext';

function App() {
  const [tokenState, setTokenState] = useState(null);
  const [userState, setUserState] = useState({
    name: '',
    email: ''
  });
  const [adminOuChat, setAdminOuChat] = useState({
    adminOuChat: '',
    groupId: '',
    groupName: ''
  })
  const [myGroupsAsMember, setMyGroupsAsMember] = useState()

  return (
    <AppContext.Provider value={ {tokenState, setTokenState, userState, setUserState, adminOuChat, setAdminOuChat, myGroupsAsMember, setMyGroupsAsMember} }>
      <main>
        { tokenState? <Accueil/> : (
          <div className="forms-container">
            <Login/>
            <Register/>
          </div>
        ) }
      </main>
    </AppContext.Provider>
  )
}

export default App
