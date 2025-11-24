import { useNavigate } from "react-router-dom"
import { useAuth } from "../contexts/AuthContext"


export const Login = () => {
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleLogin = () => {
    const username = (document.getElementById("username-input") as HTMLInputElement).value
    const password = (document.getElementById("password-input") as HTMLInputElement).value
    login(username, password)
    navigate("/")
  }

  const cancelLogin = () => {
    navigate("/")
  }

  return <div className="center-screen">
    <div className="box">
      <div className="row">
        <span className="input-label">Username:</span>
        <input id="username-input" />
      </div>
      
      <div className="row">
        <span className="input-label">Password:</span>
        <input type="password" id="password-input" />
      </div>

      <div className="button-row">
        <button>Register</button>
        <button onClick={cancelLogin}>Cancel</button>
        <button onClick={handleLogin}>Login</button>
      </div>
    </div>
  </div>
}