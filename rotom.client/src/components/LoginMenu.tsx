import { useLocation, useNavigate } from "react-router-dom";
import { NavButton } from "./NavButton";
import { useAuth } from "../contexts/AuthContext";

export const LoginMenu = () => {
  const { isLoggedIn, username, logout } = useAuth()
  const { pathname } = useLocation()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/")
  }

  return (
    <div className="login-menu">
      {isLoggedIn ? (
        <div className="user-info">
          <span className="username" style={{padding: "1rem"}}>{username}</span>
          <button className="logout-button" onClick={handleLogout}>Logout</button>
        </div>
      ) : (
        <NavButton to="/login" text="login" isActive={pathname === "/login"} />
      )}
    </div>
  );
};

// From chatgpt