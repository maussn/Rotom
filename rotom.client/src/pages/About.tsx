import { pressTestButton } from "../services/api"

export const About = () => {
  return <div className="center-screen">
    <div className="box">
      The About page
    </div>
    <div>
      <button onClick={pressTestButton}>Test</button>
    </div>
  </div>
}