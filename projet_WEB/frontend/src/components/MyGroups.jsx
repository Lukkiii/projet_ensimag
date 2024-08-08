import GroupesAdmin from './GroupesAdmin'
import GroupesMembre from './GroupesMembre'

function MyGroups() {
    return (
        <fieldset className='mygroups'>
            <legend>Mes groupes</legend>
            <GroupesMembre/>
            <GroupesAdmin/>
        </fieldset>
    )
}

export default MyGroups