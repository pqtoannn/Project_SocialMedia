import React, { useContext, useState } from 'react'
import './NavBar.css'
import HomeOutlinedIcon from '@mui/icons-material/HomeOutlined';
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined';
import DarkModeOutlinedIcon from '@mui/icons-material/DarkModeOutlined';
import NotificationsOutlinedIcon from '@mui/icons-material/NotificationsOutlined';
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import PersonOutlineOutlinedIcon from '@mui/icons-material/PersonOutlineOutlined';
import SearchOutlinedIcon from '@mui/icons-material/SearchOutlined';
import ListIcon from '@mui/icons-material/List';
import { Link } from 'react-router'
import DefaultProfilePic from '../../assets/defaultProfilePic.jpg';
import { ThemeContext } from '../../Context/ThemeContext';
import Chat from '../Chat/Chat';
import Notification from '../Notifications/Notification';
import { AuthContext } from '../../Context/AuthContext';
import { getMediaUrl } from "../../Utils/Media/getMediaUrl.js";

const NavBar = () => {

    const { darkMode, toggleDarkMode } = useContext(ThemeContext) // truy cập Context

    const { currentUser, setCurrentUser } = useContext(AuthContext)

    const [openChat, setOpenChat] = useState(false);

    const [openNotification, setOpenNotification] = useState(false);

    const handleOpenChat = () => {
        setOpenChat(!openChat)
        setOpenNotification(false)
    }

    const handleOpenNotification = () => {
        setOpenNotification(!openNotification)
        setOpenChat(false)
    }

    return (
        <div className='navbar'>
            <div className="left">
                <Link to="/">
                    <span className='logo'>Social Media</span>
                </Link>
                <Link to="/">
                    <HomeOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} />
                </Link>

                <button onClick={toggleDarkMode}>
                    {darkMode ? (
                        <WbSunnyOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} />
                    ) : (
                        <DarkModeOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} />
                    )}
                </button>
                {/* <GridViewOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} /> */}

                <div className="search">
                    <SearchOutlinedIcon className='icon text-color' />
                    <input type="text"
                        placeholder='Search...'
                        className='search-input text-color' />
                </div>
            </div>

            <div className="right">
                <div className='listIcon'>
                    <ListIcon className='text-color' style={{ fontSize: '36px' }} />
                </div>
                <Link to="/friends">
                    <PersonOutlineOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} />
                </Link>

                <EmailOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }} onClick={handleOpenChat} />
                <NotificationsOutlinedIcon className='icon text-color' style={{ fontSize: '32px' }}
                    onClick={handleOpenNotification} />

                <Link to={`/profile/${currentUser.userId}`}>
                    <div className="user">
                        <img src={getMediaUrl(currentUser.profileImageUrl) || DefaultProfilePic} alt="" className='avatar' />
                        <span className='text-[20px] text-color'>{currentUser.username}</span>
                    </div>
                </Link>
                {openChat && <Chat />}
                {openNotification && <Notification />}
            </div>
        </div>
    )
}

export default NavBar