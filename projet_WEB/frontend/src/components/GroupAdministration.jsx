import { AppContext } from '../AppContext'
import { useContext, useState, useEffect } from 'react'


function GroupAdministration() {
    const {adminOuChat, tokenState, setMyGroupsAsMember} = useContext(AppContext)
    const [allUsers, setAllUsers] = useState([])
    const [allMembers, setAllMembers] = useState([])
    const [selectedOption, setSelectedOption] = useState('')

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
        if (allUsers.length > 0) {
            fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups/' + adminOuChat.groupId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'x-access-token': tokenState
                }
            }).then(response => response.json())
              .then(data => {
                    const memberIds = data.memberIds
                    if (allUsers) {
                        const members = allUsers.filter(user => memberIds.includes(user.id))
                        setAllMembers(members)
                    }
                })
            }
    }, [adminOuChat.groupId, allUsers, tokenState])

    async function refreshMyGroupsAsMember() {
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
    }

    async function addMemberToGroup() {
        if (selectedOption === '') {
            alert('Please select a member to add')
            return
        }
        const userId = selectedOption
        const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups/' + adminOuChat.groupId + '/' + userId, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            }
        })
        const data = await response.json()
        if (data.status) {
            alert('Member added successfully')
            
            const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups/' + adminOuChat.groupId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'x-access-token': tokenState
                }
            })
            const data = await response.json()
            const memberIds = data.memberIds
            if (allUsers) {
                const members = allUsers.filter(user => memberIds.includes(user.id))
                setAllMembers(members)
            }
            refreshMyGroupsAsMember()
        } else {
            alert(data.message)
        }
    }

    async function deleteMemberFromGroup(userId) {
        const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups/' + adminOuChat.groupId + '/' + userId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'x-access-token': tokenState
            }
        })
        const data = await response.json()
        if (data.status) {
            alert('Member deleted successfully')
            
            const response = await fetch('https://tp5-6-backend.osc-fr1.scalingo.io/api/mygroups/' + adminOuChat.groupId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'x-access-token': tokenState
                }
            })
            const data = await response.json()
            const memberIds = data.memberIds
            if (allUsers) {
                const members = allUsers.filter(user => memberIds.includes(user.id))
                setAllMembers(members)
            }
            refreshMyGroupsAsMember()
        } else {
            alert(data.message)
        }
    }

    return (
        <div className='admin'>
            <fieldset>
                <legend>Administration <strong>{adminOuChat.groupName}</strong></legend>
                <div>
                    <p className="addMember">Ajouter un membre</p>
                    <select name="addUserSelect" value={selectedOption} onChange={(e) => setSelectedOption(e.target.value)}>
                        <option value="">--Choisir un membre--</option>
                        {allUsers && allUsers.map((user) => (
                            <option key={user.id} value={user.id}>{user.email}</option>
                        ))}
                    </select>
                    <button className="buttonAdd" onClick={addMemberToGroup}>Ajouter</button>
                </div>
                <div className='memberList'>
                <strong>Liste des membres</strong>
                    {allMembers && allMembers.map((member) => (
                        <div className="listEmail" key={member.email}>
                            <p>{member.email}
                                <button className="buttonDelete" onClick={() => deleteMemberFromGroup(member.id)}>Supprimer</button>
                            </p>
                        </div>
                    ))}
                </div>
            </fieldset>
        </div>
    )
}

export default GroupAdministration