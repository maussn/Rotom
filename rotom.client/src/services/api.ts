

export async function fetchHelloWorld() {
    const resp = await fetch("/api/hello")
    console.log(resp)
    const text = await resp.json()
    console.log(text)
    return text.message
}

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