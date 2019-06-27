#! /bin/bash

# small template for my bash shell scripts.

#set -o errexit  # the script ends if a command fails
#set -o pipefail # the script ends if a command fails in a pipe
#set -o nounset  # the script ends if it uses an undeclared variable
#set -o xtrace # if you want to debug

# Defaults values
logLevel=4 #4-debug;3-info;2-success;1-warning;0-error
personId=;
name=;
response_body=;
response_status_code=;
response_content_type=;


# Usage function
usage() {
  echo -n "Usage:
          get||post||patch||put||delete"
}

# Log functions
error() {
  printf "\033[0;31m%s\033[0m\n" "$1"
}
warning() {
  if [[ ${logLevel} -gt 0 ]]; then
    printf "\033[1;33m%s\033[0m\n" "$1"
  fi
}
success() {
  if [[ ${logLevel} -gt 1 ]]; then
    printf "\033[0;32m%s\033[0m\n" "$1"
  fi
}
info() {
  if [[ ${logLevel} -gt 2 ]]; then
    printf "\033[0;34m%s\033[0m\n" "$1"
  fi
}
debug() {
  if [[ ${logLevel} -gt 3 ]]; then
    printf "\033[1;30m%s\033[0m\n" "$1"
  fi
}

check() {
  local function_num_params=$#
  if [[ ${function_num_params} -lt 1 ]]; then
    error "At least one parameter must be introduced. action"
    exit 1
fi
}

# Params function
params() {
	global_action=$1
	personId=$2
	name=$3
}

# Get Person
get_person() {
    invoke_request "GET http://localhost:8008/api/v1/persons/${personId}"
    response_status_code_should_be 200
}

# Post Person
post_person(){
    invoke_request "POST -d {\"personId\":\"${personId}\"} http://localhost:8008/api/v1/persons/"
    response_status_code_should_be 201
}

# Patch Person
patch_person(){
    invoke_request "PATCH -d {\"name\":\"${name}\"} http://localhost:8008/api/v1/persons/${personId}"
    response_status_code_should_be 204
}

# PUT Person
put_person(){
    invoke_request "PUT -d {\"name\":\"${name}\",\"personId\":\"1\"} http://localhost:8008/api/v1/persons/${personId}"
    response_status_code_should_be 204
}

# DELETE Person
delete_person(){
    invoke_request "DELETE http://localhost:8008/api/v1/persons/${personId}"
    response_status_code_should_be 204
}

invoke_request () {
  request=$1
  response=$(curl -H "Content-Type: application/json"  -X ${request} -s -w \\n%{http_code}\\n%{content_type})
  request_exit_status=$?
  [[ ${request_exit_status} != 0 ]] && error "${request}" "curl exit status" "0" "${request_exit_status}"
  response_body=$(echo "${response}" | head -1)
  response_status_code=$(echo "${response}" | head -2 | tail -1)
  response_content_type=$(echo "${response}" | head -3 | tail -1)
}

response_status_code_should_be () {
  expected_response_status_code=$1
  if [[ ! "${response_status_code}" =~ ^${expected_response_status_code}$ ]]; then
    error  "response status code is ${response_status_code} but expected ${expected_response_status_code}"
   else
    success "response status code ${expected_response_status_code}"
  fi
}

execute() {
    case ${global_action} in
get)
  get_person
  ;;
post)
  post_person
  ;;
patch)
  patch_person
  ;;
put)
  put_person
  ;;
delete)
  delete_person
  ;;
*)
  usage
  exit 1
esac
exit $?
}

# Main function
main() {
  check "$@"
  params "$@"
  execute
}

main "$@"