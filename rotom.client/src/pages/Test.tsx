import { pressTestButton } from "../services/api"

export const Test = () => {
  return <div className="center-screen">
    <div className="box">
      The test page
    </div>
    <div>
      <button onClick={pressTestButton}>Test</button>
    </div>
  </div>
}