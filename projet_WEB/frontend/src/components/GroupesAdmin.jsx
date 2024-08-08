import { useEffect, useContext, useState } from "react";
import { AppContext } from "../AppContext";

function GroupesAdmin() {
    const { tokenState, adminOuChat, setAdminOuChat } = useContext(AppContext)
    const [groupsState, setGroupsState] = useState()
    const [newGroupName, setNewGroupName] = useState("")

    useEffect(() => {
        fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            }
        }).then(response => response.json())
        .then(data => {
            setGroupsState(data.groups)
        })
    });

    async function createGroup(){
        if (newGroupName === "") {
            alert("Group Name should not be empty!")
            return
        }
        fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            },
            body: JSON.stringify({
                name: newGroupName
            })
        }).then(res => res.json())
        .then(data => {
            if (data.status) {
                setGroupsState([...groupsState, data.group])
                setNewGroupName("")
            } else {
                alert(data.message)
            }
        })
    }

    return (
        <div>
            <div>
                <strong>Ceux que j&apos;administre</strong>
                <ul className="listGroupAsAdmin">
                    {groupsState && groupsState.map((group) => (
                        <li key={group.id} className={group.id === adminOuChat.groupId && adminOuChat.adminOuChat === 'admin' ? 'clicked' : ''} onClick={() => setAdminOuChat({
                            adminOuChat: 'admin',
                            groupId: group.id,
                            groupName: group.name
                        })}>{group.name}</li>
                    ))}
                </ul>
            </div>
            <div>
                <input 
                    name="newGroupName"
                    type="text" 
                    value={ newGroupName } 
                    onChange={(e) => setNewGroupName(e.target.value)}
                    placeholder="nom du nouveau groupe"
                />
                <button className="buttonCreateGroup" onClick={createGroup}>Créer</button>
            </div>
        </div>
    );
}

export default GroupesAdmin