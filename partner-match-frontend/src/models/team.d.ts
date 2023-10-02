export type TeamType={
    name:string;
    id:number;
    description:string;
    expireTime?:Date;
    maxNum:number;
    num:number;
    status?:number;
    password?:string;
    createTime:Date;
    updateTime:Date;
    createUser?:UserType;
}