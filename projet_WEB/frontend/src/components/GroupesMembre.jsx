import { useEffect, useContext } from "react";
import { AppContext } from "../AppContext";

function GroupesMembre() {
    const { tokenState, adminOuChat, setAdminOuChat, myGroupsAsMember, setMyGroupsAsMember } = useContext(AppContext)

    useEffect(() => {
        fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/groupsmember', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            }
        }).then(response => response.json())
        .then(data => {
            if (!data.status) {
                alert(data.message)
                return
            }
            setMyGroupsAsMember(data.groupsNameId)
        })
    });

    return (
        <div>
            <strong>Ceux dont je suis membre</strong>
            <ul className="listGroupAsMember">
                {myGroupsAsMember && myGroupsAsMember.map((group) => (
                    console.log(group, setAdminOuChat.groupName),
                    <li key={group.id} className={group.id === adminOuChat.groupId && adminOuChat.adminOuChat === 'chat' ? 'clicked' : ''} onClick={() => setAdminOuChat({
                        adminOuChat: 'chat',
                        groupId: group.id,
                        groupName: group.name
                    })}>{group.name}</li>
                ))}
            </ul>
        </div>
    );
}

export default GroupesMembre