import { castToItems, type LoanRequest } from "../types"

export async function sendLoginRequest(username: string, password: string) {
  const resp = await fetch("/api/login", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username,
      password: password
    })
  })
  console

  if (resp.ok) {
    const body = await resp.json()
    return body
  } else {
    return {
      statusCode: resp.status,
      statusText: resp.statusText
    }
  }
}

export async function getCatalogue() {
  const resp = await fetch("/api/catalogue")
  if (resp.ok) {
    const body = await resp.json()
    return castToItems(body.items)
  }
}

export async function getCatalogueLoggedIn(userId: string) {
  const resp = await fetch("/api/catalogue/" + userId)
  if (resp.ok) {
    const body = await resp.json()
    return castToItems(body.items)
  }
}

export async function pressTestButton() {
  const resp = await fetch("/api/test")
  const body = await resp.json()
  console.log(body)
}

export async function postLoanRequest(loanRequest: LoanRequest ) {
  const resp = await fetch("/api/loan", {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(loanRequest)
  })
  console.log(resp)
  return {
    statusCode: resp.status,
    statusText: resp.statusText
  }
}