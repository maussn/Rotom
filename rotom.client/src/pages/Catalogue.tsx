import { getCatalogue } from "../services/api"


export const Catalogue = () => {
  return <div className="center-screen">
    <div className="box">
      <button onClick={getCatalogue}>catalogue</button>
    </div>
  </div>
}