export interface PayCongress {
    userId: number,
    congressId: number,
    date: string
}

export interface Inscription {
    congressId: number,
    congressName: string,
    userId: number,
    userName: string,
    attendeeRolName: string
}

export interface Pay {
    id: number,
    userId: number,
    userName: string,
    congressId: number,
    congressName:string,
    total: number,
    date: string
}