import {useContext} from 'react'
import { AppContext } from '../AppContext'
import Chat from './Chat'
import GroupAdministration from './GroupAdministration'

function Extra() {
    const {adminOuChat} = useContext(AppContext)

    return (
        <div>
            {adminOuChat.adminOuChat === 'admin'? <GroupAdministration/> : null}
            {adminOuChat.adminOuChat === 'chat'? <Chat/> : null}
        </div> 
    )
}

export default Extra